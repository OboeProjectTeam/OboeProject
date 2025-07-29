  // src/firebase.js
  import firebase from 'firebase/compat/app';
  import 'firebase/compat/auth';

  const firebaseConfig = {
    apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
    authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
    projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
    storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
    appId: import.meta.env.VITE_FIREBASE_APP_ID,
    measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID,
  };

  // Initialize Firebase only if it hasn't been initialized yet
  try {
    if (!firebase.apps?.length) {
      firebase.initializeApp(firebaseConfig);
    }
  } catch (error) {
    console.error("Firebase initialization error", error);
  }

  export const auth = firebase.auth();
  export { firebase };