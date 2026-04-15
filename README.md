# Sewa SMS - Application Android

**Sewa SMS** est une application mobile innovante qui convertit les fichiers audio en messages SMS encodés en Base64, permettant l'envoi d'audio même dans les régions où les MMS ne fonctionnent pas (comme en Afrique).

## 📱 Fonctionnalités

✅ **Envoi de SMS et d'audio encodé**
- Enregistrement audio directement dans l'app
- Conversion automatique en Base64 pour SMS
- Envoi par SMS standard

✅ **Réception et décodage**
- Détection automatique des messages audio
- Décodage Base64 → Fichier audio
- Stockage local des messages

✅ **Interface SMS classique**
- Vue des conversations
- Historique des messages
- Notifications à la réception

✅ **Audio avancé**
- Compression audio avant envoi
- Enregistrement HQ (44.1kHz, AAC)
- Compression auto (22.05kHz, mono, 32kbps)

✅ **Thème dynamique**
- Support clair/sombre
- Basculement en temps réel

✅ **Base de données**
- SQLite local
- Stockage conversations
- Stockage messages et audio

## 🚀 Installation

### Prérequis
- Android Studio Flamingo ou plus récent
- Android SDK 26+ (Android 8.0+)
- JDK 11 ou plus récent
- Gradle 8.1+

### Étapes d'installation

1. **Cloner le repo**
```bash
cd /home/bria/site
```

2. **Ouvrir dans Android Studio**
   - File → Open Project → Sélectionner le dossier `/home/bria/site`
   - Attendre la synchronisation Gradle

3. **Compiler et déployer**
```bash
# Via Android Studio
- Select Device → Run

# Ou via CLI
./gradlew installDebug
```

4. **Premiers tests**
   - Accorder les permissions
   - Créer une nouvelle conversation (FAB +)
   - Enregistrer un audio
   - Envoyer par SMS

## 📁 Structure du projet

```
SewaSMS/
├── app/
│   ├── src/main/
│   │   ├── java/com/sewasms/
│   │   │   ├── MainActivity.kt          # Liste des conversations
│   │   │   ├── ChatActivity.kt          # Écran de chat
│   │   │   ├── SplashActivity.kt        # Écran de démarrage
│   │   │   ├── NewConversationDialog.kt # Dialog nouveau message
│   │   │   ├── models/
│   │   │   │   ├── Message.kt
│   │   │   │   └── Conversation.kt
│   │   │   ├── utils/
│   │   │   │   ├── AudioConverter.kt    # Base64 encoding
│   │   │   │   ├── AudioRecorder.kt     # Enregistrement
│   │   │   │   ├── AudioCompressor.kt   # Compression
│   │   │   │   ├── SMSManager.kt        # Envoi SMS
│   │   │   │   ├── DatabaseHelper.kt    # SQLite
│   │   │   │   ├── NotificationHelper.kt# Notifications
│   │   │   │   └── ThemeManager.kt      # Thème
│   │   │   ├── adapters/
│   │   │   │   ├── MessageAdapter.kt
│   │   │   │   └── ConversationAdapter.kt
│   │   │   └── receivers/
│   │   │       └── SMSReceiver.kt       # Réception SMS
│   │   ├── res/
│   │   │   ├── layout/         # Fichiers XML de mise en page
│   │   │   ├── drawable/       # Ressources images
│   │   │   ├── values/         # Strings, colors, styles
│   │   │   └── values-night/   # Thème sombre
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## 🔧 Configuration

### Permissions requises
L'app demande les permissions suivantes:
- `RECORD_AUDIO` - Enregistrement audio
- `SEND_SMS` - Envoi de SMS
- `RECEIVE_SMS` - Réception de SMS
- `READ_SMS` - Lecture de SMS
- `READ_CONTACTS` - Lecture des contacts
- `READ/WRITE_EXTERNAL_STORAGE` - Accès fichiers
- `POST_NOTIFICATIONS` - Notifications

### Dépendances principales
```gradle
- androidx.appcompat:1.6.1
- androidx.material:1.9.0
- androidx.recyclerview:1.3.0
- androidx.lifecycle:2.6.1
- androidx.room:2.5.2 (database)
- kotlinx.coroutines:1.7.1 (async)
```

## 📊 Format des données

### Envoi d'audio par SMS
```
````AUDIO
[Base64EncodedAudioData]
```

### Détection à la réception
- Format détecté automatiquement
- Marqueur: `````AUDIO`
- Décodage automatique
- Sauvegarde locale en .m4a

## 🎨 Themes

### Light Mode (défaut)
- Couleur primaire: #2E7D32 (Vert)
- Couleur secondaire: #4CAF50

### Dark Mode
- Couleur primaire: #1B5E20 (Vert foncé)
- Fond: #121212 (Noir)

**Basculer le thème:** Cliquer l'icône engrenage en haut à droite

## ⚙️ Compilation et release

### Debug
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Release
```bash
./gradlew bundleRelease
# Bundle: app/build/outputs/bundle/release/app-release.aab
```

## 🐛 Troubleshooting

**Problème:** "Cannot connect to SMS"
- Solution: Vérifier les permissions
- Vérifier que l'appareil a un port SMS actif

**Problème:** "Audio compression fails"
- Solution: Fichier audio trop volumineux
- Vérifier espace disque disponible

**Problème:** "Notifications ne s'affichent pas"
- Solution: Vérifier permission POST_NOTIFICATIONS
- Vérifier les paramètres de notification du système

## 📝 Changelog

### v1.0 (Initial)
- ✅ Envoi/réception SMS
- ✅ Enregistrement audio
- ✅ Compression audio
- ✅ Notifications
- ✅ Thème clair/sombre
- ✅ Base de données locale


**Sewa SMS** - SMS Audio Pour Tous 🎤📱
