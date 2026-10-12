package com.example.nfcreader

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Mini-lecteur NFC — l'équivalent de la fonction "Read" d'un Flipper, en natif Android.
 *
 * Principe : quand l'app est au premier plan, on active le "Reader Mode". Dès qu'un tag
 * passe devant le dos du téléphone, le système appelle onTagDiscovered() et on y lit :
 *   - l'UID (identifiant unique du tag)
 *   - la liste des technologies supportées (MIFARE, NDEF, ISO-DEP...)
 *   - le contenu NDEF (texte / URL) s'il y en a un
 */
class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // On construit l'écran directement en code pour n'avoir aucun fichier de layout à gérer.
        val scroll = ScrollView(this)
        output = TextView(this).apply {
            textSize = 16f
            setPadding(40, 60, 40, 60)
            text = "Approche un tag NFC du dos du téléphone…"
        }
        scroll.addView(output)
        setContentView(scroll)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        when {
            nfcAdapter == null ->
                output.text = "❌ Ce téléphone n'a pas de puce NFC."
            nfcAdapter?.isEnabled == false ->
                output.text = "⚠️ Le NFC est désactivé. Active-le dans les réglages puis reviens."
        }
    }

    override fun onResume() {
        super.onResume()
        // On active la lecture seulement quand l'app est visible.
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
        nfcAdapter?.enableReaderMode(this, this, flags, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    /** Appelé par le système à chaque tag détecté. On est sur un thread secondaire ici. */
    override fun onTagDiscovered(tag: Tag) {
        val sb = StringBuilder()

        // 1) UID : l'identifiant du tag, en hexadécimal
        sb.append("✅ Tag détecté\n\n")
        sb.append("UID : ${bytesToHex(tag.id)}\n")
        sb.append("UID (${tag.id.size} octets)\n\n")

        // 2) Technologies supportées
        sb.append("Technologies :\n")
        for (tech in tag.techList) {
            sb.append("  • ${tech.substringAfterLast('.')}\n")
        }
        sb.append("\n")

        // 3) Contenu NDEF (texte / URL) si le tag en contient
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            try {
                ndef.connect()
                val message = ndef.ndefMessage
                if (message != null) {
                    sb.append("Contenu NDEF :\n")
                    for (record in message.records) {
                        sb.append("  → ${decodeRecord(record)}\n")
                    }
                } else {
                    sb.append("Tag NDEF vierge (aucune donnée écrite).\n")
                }
                ndef.close()
            } catch (e: Exception) {
                sb.append("Lecture NDEF impossible : ${e.message}\n")
            }
        } else {
            sb.append("Pas de données NDEF (tag brut ou protégé).\n")
        }

        // On met à jour l'écran sur le thread principal.
        runOnUiThread { output.text = sb.toString() }
    }

    /** Convertit des octets bruts en chaîne hexadécimale lisible : AF 3C 01 ... */
    private fun bytesToHex(bytes: ByteArray): String =
        bytes.joinToString(" ") { "%02X".format(it) }

    /** Décode un enregistrement NDEF en texte lisible (gère le texte simple et les URL). */
    private fun decodeRecord(record: android.nfc.NdefRecord): String {
        val payload = record.payload
        return try {
            when {
                // Enregistrement de type "URI" (URL)
                record.toUri() != null -> record.toUri().toString()
                // Enregistrement de type "Texte"
                payload.isNotEmpty() -> {
                    // On saute l'octet de statut + le code langue pour les enregistrements texte
                    val languageCodeLength = payload[0].toInt() and 0x3F
                    String(
                        payload,
                        languageCodeLength + 1,
                        payload.size - languageCodeLength - 1,
                        Charsets.UTF_8
                    )
                }
                else -> "(vide)"
            }
        } catch (e: Exception) {
            String(payload, Charsets.UTF_8)
        }
    }
}
