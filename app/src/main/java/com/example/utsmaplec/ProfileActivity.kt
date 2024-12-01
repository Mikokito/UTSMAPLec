package com.example.utsmaplec

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Bottom Navigation View setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Pindah ke HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_calendar -> {
                    // Pindah ke CalendarActivity
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Sudah di ProfileActivity, tidak perlu start ulang
                    true
                }
                else -> false
            }
        }

        // Set the default selected item to profile
        bottomNavigationView.selectedItemId = R.id.nav_profile

        // Log out functionality
        val logoutTextView = findViewById<TextView>(R.id.logout_text)
        logoutTextView.setOnClickListener {
            performLogout()
        }

        // Handle Change Account Name Click
        val changeAccountNameLayout = findViewById<LinearLayout>(R.id.change_account_name_layout)
        changeAccountNameLayout.setOnClickListener {
            showChangeAccountNameDialog()
        }

        // Handle Change Account Image Click
        val changeAccountImageLayout = findViewById<LinearLayout>(R.id.change_account_image_layout)
        changeAccountImageLayout.setOnClickListener {
            showChangeAccountImageDialog()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            openCamera()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // Izin kamera tidak diberikan, tampilkan pesan ke pengguna
            }
        }
    }

    private fun performLogout() {
        // Tampilkan pop-up konfirmasi log out
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout_confirmation, null)
        val cancelText = dialogView.findViewById<TextView>(R.id.dialog_cancel)
        val confirmText = dialogView.findViewById<TextView>(R.id.dialog_confirm)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Handle tombol No
        cancelText.setOnClickListener {
            dialog.dismiss() // Tutup dialog
        }

        // Handle tombol Yes
        confirmText.setOnClickListener {
            // Hapus status login dari SharedPreferences
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("USER_PREF", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // Hapus semua data
            editor.apply()

            // Arahkan pengguna kembali ke StartActivity
            val intent = Intent(this, StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showChangeAccountNameDialog() {
        // Inflate custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_account_name, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_account_name)
        val cancelText = dialogView.findViewById<TextView>(R.id.dialog_cancel)
        val editTextButton = dialogView.findViewById<TextView>(R.id.dialog_edit)

        // Create AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Handle Cancel button
        cancelText.setOnClickListener {
            dialog.dismiss()
        }

        // Handle Edit button
        editTextButton.setOnClickListener {
            val newName = editText.text.toString()
            if (newName.isNotEmpty()) {
                // Simpan nama baru ke SharedPreferences
                val sharedPreferences: SharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("ACCOUNT_NAME", newName)
                editor.apply()

                // Update nama di layar utama
                val profileName = findViewById<TextView>(R.id.profile_name)
                profileName.text = newName

                dialog.dismiss()
            } else {
                editText.error = "Name cannot be empty"
            }
        }

        dialog.show()
    }

    private fun showChangeAccountImageDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_account_image, null)
        val takePicture = dialogView.findViewById<TextView>(R.id.dialog_take_picture)
        val chooseFromGallery = dialogView.findViewById<TextView>(R.id.dialog_choose_gallery)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        takePicture.setOnClickListener {
            checkCameraPermission() // Periksa izin kamera sebelum membuka kamera
            dialog.dismiss()
        }

        chooseFromGallery.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI = createImageFile()?.let {
            FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
        }

        if (photoURI != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            File.createTempFile("temp_image", ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    // Tampilkan gambar di ImageView atau simpan gambar
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    // Tampilkan gambar dari galeri di ImageView atau simpan URI
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_CAMERA_PERMISSION = 100
    }

}
