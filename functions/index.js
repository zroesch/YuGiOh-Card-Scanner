const { onSchedule } = require("firebase-functions/v2/scheduler");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore, FieldValue } = require("firebase-admin/firestore");
const { getStorage } = require("firebase-admin/storage");
const axios = require("axios");
const csv = require("csv-parser");
const { v4: uuidv4 } = require("uuid");

initializeApp();
const db = getFirestore();
const storage = getStorage().bucket();

const BASE_URL = "https://tcgcsv.com/tcgplayer/2/";

// ✅ Upload Set Image and Return URL
async function uploadSetImageToFirebase(imageUrl, groupId, productId) {
    if (!imageUrl || !imageUrl.startsWith("http")) return null;
    try {
        const response = await axios.get(imageUrl, { responseType: "arraybuffer", validateStatus: (status) => status < 400 });
        const buffer = Buffer.from(response.data);
        const fileName = `set-images/${groupId}_${productId}.jpg`;

        const file = storage.file(fileName);
        await file.save(buffer, { contentType: "image/jpeg", metadata: { firebaseStorageDownloadTokens: uuidv4() } });

        return `https://storage.googleapis.com/${storage.name}/${fileName}`;
    } catch (error) {
        return null;
    }
}

// ✅ Upload Individual Card Images to Firebase Storage
async function uploadImageToFirebase(imageUrl, productId) {
    if (!imageUrl || !imageUrl.startsWith("http")) return null;
    try {
        const response = await axios.get(imageUrl, { responseType: "arraybuffer", validateStatus: (status) => status < 400 });
        const buffer = Buffer.from(response.data);
        const fileName = `product-images/${productId}.jpg`;

        const file = storage.file(fileName);
        await file.save(buffer, { contentType: "image/jpeg", metadata: { firebaseStorageDownloadTokens: uuidv4() } });

        return `https://storage.googleapis.com/${storage.name}/${fileName}`;
    } catch (error) {
        return null;
    }
}

// ✅ Fetch and Update Products in Firestore Efficiently
async function fetchAndUpdateProducts(groupId, groupName) {
    try {
        const response = await axios.get(`${BASE_URL}${groupId}/ProductsAndPrices.csv`, { responseType: "stream" });

        return new Promise((resolve, reject) => {
            let rowCount = 0;
            const products = [];
            let setImageUrls = [];

            response.data
                .pipe(csv())
                .on("data", async (row) => {
                    const productId = row.productId?.trim();
                    if (!productId) return;

                    const isPackOrCollection = !row.extNumber?.trim() && !row.extRarity?.trim();
                    if (isPackOrCollection) {
                        setImageUrls.push(uploadSetImageToFirebase(row.imageUrl, groupId, productId));
                        return;
                    }

                    rowCount++;
                    products.push({
                        productId,
                        name: row.name,
                        cleanName: row.cleanName,
                        imageUrl: row.imageUrl,
                        categoryId: parseInt(row.categoryId),
                        groupId: parseInt(row.groupId),
                        extNumber: row.extNumber,
                        extRarity: row.extRarity,
                        marketPrice: parseFloat(row.marketPrice || "0"),
                        setName: groupName,
                    });
                })
                .on("end", async () => {
                    setImageUrls = await Promise.all(setImageUrls);
                    setImageUrls = setImageUrls.filter(url => typeof url === "string" && url !== null);

                    const groupRef = db.collection("categories").doc("2").collection("sets").doc(groupId);
                    const existingSet = await groupRef.get();

                    const newSetData = {
                        name: groupName,
                        totalCards: rowCount,
                        setImageUrls: setImageUrls,
                        lastUpdated: FieldValue.serverTimestamp(),
                    };

                    if (!existingSet.exists || JSON.stringify(existingSet.data()) !== JSON.stringify(newSetData)) {
                        await groupRef.set(newSetData, { merge: true });
                    }

                    const batch = db.batch();
                    for (const product of products) {
                        product.storageUrl = await uploadImageToFirebase(product.imageUrl, product.productId);
                        const productRef = groupRef.collection("products").doc(product.productId);
                        batch.set(productRef, product, { merge: true });
                    }

                    await batch.commit();
                    resolve(products);
                    console.log(`✅ ${products.length} cards saved for set: ${groupId}`);
                })
                .on("error", reject);
        });
    } catch (error) {
        return [];
    }
}

// ✅ Efficient Daily Scheduled Function (Runs at 20:00:00 UTC)
exports.updateFirestoreAndStorage = onSchedule("every day 20:00", async () => {
    try {
        const response = await axios.get(`${BASE_URL}Groups.csv`, { responseType: "stream" });

        const groups = [];
        await new Promise((resolve, reject) => {
            response.data
                .pipe(csv())
                .on("data", (row) => {
                    groups.push({ groupId: row.groupId, name: row.name });
                })
                .on("end", resolve)
                .on("error", reject);
        });

        for (const group of groups) {
            await fetchAndUpdateProducts(group.groupId, group.name);
        }

        console.log("\n🎉 Firestore & Storage update complete!");
    } catch (error) {
        console.error("❌ Error updating Firestore:", error.message);
    }
});
