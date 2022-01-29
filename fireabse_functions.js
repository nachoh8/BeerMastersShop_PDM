const functions = require('firebase-functions');
var admin = require('firebase-admin');
admin.initializeApp();
const path = require('path');


//Products
exports.updateBrand = functions.firestore
.document('Products/{prodId}')
.onWrite((snap, context) => {
  const existsBefore = snap.before.exists;
  const existsAfter = snap.after.exists;

  let brandId;
  let incrementActive = 0;
  if (existsAfter && !existsBefore) { //onCreate
    const active = snap.after.data().active;
    brandId = snap.after.data().brandId;
    if (active) {
      incrementActive = 1;
    }
  } else if (!existsAfter && existsBefore) { //onDelete
    const activeBefore = snap.before.data().active;
    brandId = snap.before.data().brandId;
    if (activeBefore) {
      incrementActive = -1;
    }
  } else if (existsAfter && existsBefore) { //onUpdate
    const active = snap.after.data().active;
    const activeBefore = snap.before.data().active;
    brandId = snap.after.data().brandId;
    if (!activeBefore && active) {
      incrementActive = 1;
    } else if (activeBefore && !active) {
      incrementActive = -1;
    } else {
      return null;
    }
  } else {
    return null;
  }

  const db = admin.firestore();

  return db.collection('Brands').doc(brandId).get()
  .then(doc => {
      if (!doc.exists) {
        console.log("Brand not exists");
        return null;
      }

      let newActives = (doc.data().numActive || 0) + incrementActive;
      let newTotal = (doc.data().numTotal || 0);
      if (existsAfter && !existsBefore) { //onCreate
        newTotal = newTotal + 1;
      } else if (!existsAfter && existsBefore) { //onDelete
        newTotal = newTotal - 1;
      }
      if (newActives < 0) {
        newActives = 0;
      }
      if (newTotal < 0) {
        newTotal = 0;
      }

      const promises = []
      promises.push(doc.ref.update({numTotal: newTotal}));
      promises.push(doc.ref.update({numActive: newActives}));

      return Promise.all(promises);
  }).catch(err => {
    console.log('Error getting brand', err);
    return null;
  });
});

exports.deleteImg = functions.firestore
.document('Products/{prodId}')
.onDelete((snap, context) => {
  const prodId = context.params.prodId;
  const bucket = admin.storage().bucket();
  const file = bucket.file('Products/'+prodId);

  return file.delete();
});

//Orders
exports.updateSales = functions.firestore
.document('Orders/{orders_userId}/MyOrders/{orderId}/Products/{productId}')
.onCreate((snap, context) => {
  const prodId = snap.data().docId;
  const numUd = snap.data().numUd;
  const refOrdersUser = context.params.orders_userId;
  const orderId = context.params.orderId;
  
  const db = admin.firestore();
  const refOrder = db.collection('Orders').doc(refOrdersUser).collection('MyOrders').doc(orderId);
  const refProd =  db.collection('Products').doc(prodId)

  return refOrder.get()
  .then(doc => {
    let update = true;
    if (doc.exists) {
      let success = doc.data().success;
      console.log('order success: ', success);
      update = success;
    }
    if (update) {
      return refProd.get()
      .then(doc => {
        if (!doc.exists) {
          console.log("Product not exists");
          return null;
        }
        let newSales = (doc.data().sales || 0) + numUd;
        let update = doc.ref.update({sales: newSales});
        return Promise.resolve(update)
      });
    } else {
      return null;
    }
  }).catch(err => {
    console.log('Error updating sales', err);
    return null;
  });
});

exports.updateProdPurchased = functions.firestore
.document('Orders/{orders_userId}/MyOrders/{orderId}/Products/{productId}')
.onCreate((snap, context) => {
  const prodId = snap.data().docId;
  const numUd = snap.data().numUd;
  const refOrdersUser = context.params.orders_userId;
  const orderId = context.params.orderId;

  const db = admin.firestore();
  const refOrder = db.collection('Orders').doc(refOrdersUser).collection('MyOrders').doc(orderId);
  const ref = db.collection('Orders').doc(refOrdersUser).collection('ProductsPurchased').doc(prodId);
  
  return refOrder.get()
  .then(doc => {
    let addProds = true;
    if (doc.exists) {
      let success = doc.data().success;
      console.log('order success: ', success)
      addProds = success;
    }
    if (addProds) {
      return ref.get()
      .then(doc => {
        if (!doc.exists) {
          let create = doc.ref.set({numUd: numUd});
          return Promise.resolve(create);
        } else {
          let newNumUd = (doc.data().numUd || 0) + numUd;
          let update = doc.ref.update({numUd: newNumUd});
          return Promise.resolve(update)
        }
      });
    } else {
      return null;
    }
  }).catch(err => {
    console.log('Error Updating product purchased', err);
    return null;
  });
});

// Reviews
exports.addReview = functions.firestore
.document('Reviews/{reviewId}')
.onCreate((snap, context) => {
    const newValue = snap.data();
    const prodId = newValue.prodId;
    const rating = newValue.rating;

    const db = admin.firestore();

    return db.collection('Products').doc(prodId).get()
    .then(doc => {
      if (doc.exists) {
            let nR = (doc.data().numReviews || 0);
            let newNumRev = nR  + 1;
            let rAcum = (doc.data().ratingAcum || 0); 
            let acum = rAcum + rating;
            console.log('oldNumReviews: ', nR, ' | newNumReviews: ', newNumRev)
            console.log('oldAcum: ', rAcum, ' | newAcum: ', acum)
            return Promise.resolve(doc.ref.update({numReviews: newNumRev, ratingAcum: acum}))
            //let newRating = acum/newNumRev;
            //const promises = [];
            //promises.push(doc.ref.update({numReviews: newNumRev}));
            //promises.push(doc.ref.update({ratingAcum: acum}));
            //promises.push(doc.ref.update({rating: newRating}));
            //return Promise.all(promises);
      } else {
        return null;
      }
    }).catch(err => {
      console.log('Update rating Failure:', err);
      throw new Error("Update rating failure");
    });
});

exports.removeReview = functions.firestore
.document('Reviews/{reviewId}')
.onDelete((snap, context) => {
  const rating = snap.data().rating;
  const prodId = snap.data().prodId;

  const db = admin.firestore();

  return db.collection('Products').doc(prodId).get()
    .then(doc => {
      if (doc.exists) {
        let nr = doc.data().numReviews;
        let r = doc.data().ratingAcum
        let newNumRev = (doc.data().numReviews || 0) - 1;
        let acum = (doc.data().ratingAcum || 0) - rating;
        console.log('num rev: ', nr, ' acum: ', r);
        let newRating;
        if (acum > 0 && newNumRev > 0) {
          newRating = acum/newNumRev;
        } else {
          acum = 0;
          newRating = 0;
          newNumRev = 0;
        }

        const promises = [];
        promises.push(doc.ref.update({numReviews: newNumRev}));
        promises.push(doc.ref.update({ratingAcum: acum}));
        promises.push(doc.ref.update({rating: newRating}));
        return Promise.all(promises);
      } else {
        return null;
      }
    }).catch(err => {
      console.log('Update rating Failure:', err);
      throw new Error("Update rating failure");
    });
  /*let prodRef = db.collection('Products').doc(prodId);
  db.runTransaction(t => {
        return t.get(prodRef)
        .then(doc => {
            let nr = doc.data().numReviews;
            let r = doc.data().ratingAcum
            let newNumRev = (doc.data().numReviews || 0) - 1;
            let acum = (doc.data().ratingAcum || 0) - rating;
            console.log('num rev: ', nr, ' acum: ', r);
            let newRating;
            if (acum > 0 && newNumRev > 0) {
              newRating = acum/newNumRev;
            } else {
              acum = 0;
              newRating = 0;
            }
            const promises = [];
            promises.push(t.update(prodRef, {numReviews: newNumRev}));
            promises.push(t.update(prodRef, {ratingAcum: acum}));
            promises.push(t.update(prodRef, {rating: newRating}));
            return Promise.all(promises);
        });
    }).then(result => {
        console.log('Transaction success!');
        return "";
      }).catch(err => {
        console.log('Transaction failure:', err);
        throw new Error("Transaction failure");
      });*/
});

exports.changeName = functions.firestore
.document('Users/{userId}')
.onUpdate((snap, context) => {
  const newValue = snap.after.data().name;
  const previousValue = snap.before.data().name;

  if (newValue === previousValue) {
    return null;
  }

  const db = admin.firestore();
  return db.collection('Reviews').where('userId', '==', snap.before.id).get()
  .then(snapshot => {
      if (snapshot.empty) {
        console.log("No have reviews");
        return null;
      }
      const promises = []
      snapshot.forEach(doc => {
        promises.push(doc.ref.update({
          userName: newValue
        }));
      });
      return Promise.all(promises);
  }).catch(err => {
    console.log('Error getting docs reviews', err);
    return null;
  });


});

exports.changeProdName = functions.firestore
.document('Products/{prodId}')
.onUpdate((snap, context) => {
  const newValue = snap.after.data().name;
  const previousValue = snap.before.data().name;

  if (newValue === previousValue) {
    return null;
  }

  const db = admin.firestore();
  return db.collection('Reviews').where('prodId', '==', context.params.prodId).get()
  .then(snapshot => {
      if (snapshot.empty) {
        console.log("No have reviews");
        return null;
      }
      const promises = []
      snapshot.forEach(doc => {
        promises.push(doc.ref.update({
          prodName: newValue
        }));
      });
      return Promise.all(promises);
  }).catch(err => {
    console.log('Error getting docs reviews', err);
    return null;
  });


});