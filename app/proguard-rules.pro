# Aturan ini ditambahkan oleh Android Studio secara default.
# Jangan hapus atau ubah baris ini.
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-assumevalues class android.os.Build$VERSION {
    public static final int SDK_INT return 21..34;
}

# Jaga agar semua Fragment yang digunakan tidak dihapus atau diubah namanya.
-keep class * extends androidx.fragment.app.Fragment { *; }
-keep class * extends androidx.fragment.app.DialogFragment { *; }

# Jaga agar semua Activity tidak dihapus.
-keep public class * extends android.app.Activity

# Jaga agar nama class View Binding tidak diubah agar tetap bisa digunakan.
-keep class * implements androidx.viewbinding.ViewBinding

# Jika Anda menggunakan data class (seperti OnboardingItem), jaga agar tidak diubah
# agar proses serialisasi (jika ada) tidak error.
-keepclassmembers class com.example.skindiagnosisai.OnboardingItem {
    <fields>;
    <init>(...);
}

# Banyak library populer (seperti Retrofit, Glide, Gson) memerlukan aturan -keep
# mereka sendiri. Biasanya aturan ini bisa ditemukan di dokumentasi resmi library tersebut.