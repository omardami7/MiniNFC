# Mini NFC — lecteur de tags NFC pour Android

Petite application Android qui lit un tag NFC et affiche son **UID**, ses
**technologies** et son **contenu texte / URL**. C'est la brique de base d'un
projet « type Flipper » en pur logiciel, sans aucun matériel externe.

## Comment obtenir l'APK à installer

Tu as deux chemins. Le premier ne demande **rien à installer** sur ton ordinateur.

### Chemin A — GitHub compile l'APK pour toi (recommandé, gratuit)

1. Crée un compte sur https://github.com (gratuit) si tu n'en as pas.
2. Crée un nouveau dépôt (bouton **New**), nomme-le `MiniNFC`, laisse-le public, clique **Create**.
3. Sur la page du dépôt vide, clique **« uploading an existing file »**.
4. Glisse-dépose **tout le contenu de ce dossier** (garde l'arborescence : le
   dossier `app/`, le dossier `.github/`, `gradlew`, etc.). Valide avec **Commit changes**.
5. Va dans l'onglet **Actions** du dépôt. Un build nommé « Build APK » se lance tout seul.
6. Attends le petit ✅ vert (2–3 min), clique sur le build, puis télécharge
   l'artefact **MiniNFC-debug-apk** en bas de la page. Dedans : `app-debug.apk`.
7. Envoie ce fichier sur ton téléphone (câble, Drive, mail…), ouvre-le, autorise
   l'installation depuis cette source, installe. C'est fait.

### Chemin B — Android Studio (si tu veux coder et modifier)

1. Installe Android Studio : https://developer.android.com/studio
2. **File → Open**, choisis ce dossier `MiniNFC`, attends la synchronisation Gradle.
3. Branche ton téléphone (débogage USB activé) et clique le triangle vert ▶.

## Activer l'installation d'un APK « hors Play Store »

Au moment d'ouvrir le fichier `.apk`, Android demande d'autoriser
« installer des applications inconnues » pour l'appli qui ouvre le fichier
(ton gestionnaire de fichiers ou navigateur). Accepte, puis relance l'installation.

## Utilisation

Ouvre l'appli, approche un tag NFC (badge, autocollant NFC, carte de transport,
carte bancaire sans contact…) du haut du dos du téléphone. L'UID et le reste
s'affichent aussitôt.

> Sur une carte bancaire, tu verras l'UID et les technologies, mais pas le
> numéro ni les données secrètes : c'est protégé, et c'est normal.

## Structure

```
MiniNFC/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/nfcreader/MainActivity.kt   ← tout le code
│       └── res/values/strings.xml
├── .github/workflows/build.yml   ← compile l'APK sur GitHub
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradlew, gradle/              ← wrapper (ne pas supprimer)
```
