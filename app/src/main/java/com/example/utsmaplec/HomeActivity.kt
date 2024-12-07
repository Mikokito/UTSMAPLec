package com.example.utsmaplec

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.utsmaplec.model.Task

class HomeActivity : AppCompatActivity() {

    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inisialisasi komponen
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_tasks)

        // Setup RecyclerView
        taskAdapter = TaskAdapter(taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Setup Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Tetap di Home
                    true
                }
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    // Tampilkan dialog untuk menambahkan Task
                    showAddTaskDialog()
                    true
                }
                R.id.nav_alarm -> {
                    // Intent ke AlarmActivity (kalau diperlukan)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Set default menu yang dipilih
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun showAddTaskDialog() {
        // Inflate layout dialog_add_task
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Inisialisasi komponen dalam dialog
        val editTaskTitle = dialogView.findViewById<EditText>(R.id.edit_task_title)
        val editTaskDescription = dialogView.findViewById<EditText>(R.id.edit_task_description)
        val btnSubmitTask = dialogView.findViewById<Button>(R.id.btn_submit_task)

        btnSubmitTask.setOnClickListener {
            val title = editTaskTitle.text.toString()
            val description = editTaskDescription.text.toString()

            if (title.isNotEmpty()) {
                // Tambahkan task ke dalam daftar
                taskList.add(Task(title, description))
                taskAdapter.notifyItemInserted(taskList.size - 1)
                dialog.dismiss() // Tutup dialog
            } else {
                editTaskTitle.error = "Task title cannot be empty"
            }
        }

        // Tampilkan dialog
        dialog.show()
    }
}