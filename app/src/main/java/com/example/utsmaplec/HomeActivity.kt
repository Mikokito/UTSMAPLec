package com.example.utsmaplec

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.utsmaplec.model.Task
import android.graphics.*
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    val list = arrayListOf<TodoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSwipe()

        db.todoDao().getTask().observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })


    }

    fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.getItemId(position))
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap

                    if (dX > 0) {


                        paint.color = Color.parseColor("#388E3C")

                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                        )




                    } else {

                        paint.color = Color.parseColor("#D32F2F")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )


                    }
                    viewHolder.itemView.translationX = dX


                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }


        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTodo(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    fun displayTodo(newText: String = "") {
        db.todoDao().getTask().observe(this, Observer {
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(newText,true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openNewTask(view: View) {
        startActivity(Intent(this, TaskActivity::class.java))
    }
}

//    private val taskList = mutableListOf<Task>()
//    private lateinit var taskAdapter: TaskAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//        // Inisialisasi komponen
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_tasks)
//
//        // Setup RecyclerView
//        taskAdapter = TaskAdapter(taskList)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = taskAdapter
//
//        // Update visibilitas berdasarkan data
//        updateVisibility()
//
//        // Contoh menambahkan data
//        taskAdapter.updateTasks(taskList) // Ganti dengan data Anda
//        updateVisibility()
//
//        // Setup Bottom Navigation
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> {
//                    // Tetap di Home
//                    true
//                }
//                R.id.nav_calendar -> {
//                    startActivity(Intent(this, CalendarActivity::class.java))
//                    true
//                }
//                R.id.nav_add -> {
//                    // Tampilkan dialog untuk menambahkan Task
//                    showAddTaskDialog()
//                    true
//                }
//                R.id.nav_alarm -> {
//                    // Intent ke AlarmActivity (kalau diperlukan)
//                    true
//                }
//                R.id.nav_profile -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Set default menu yang dipilih
//        bottomNavigationView.selectedItemId = R.id.nav_home
//    }
//
//    // Fitur kalau task kosong recycler ilang
//    private fun updateVisibility() {
//        val emptyImage: View = findViewById(R.id.empty_image)
//        val emptyTitle: View = findViewById(R.id.empty_title)
//        val emptySubtitle: View = findViewById(R.id.empty_subtitle)
//        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_tasks)
//
//        if (taskAdapter.itemCount > 0) {
//            // Jika ada data, tampilkan RecyclerView
////            recyclerView.visibility = View.VISIBLE
//            emptyImage.visibility = View.GONE
//            emptyTitle.visibility = View.GONE
//            emptySubtitle.visibility = View.GONE
//        } else {
//            // Jika tidak ada data, tampilkan gambar dan teks
////            recyclerView.visibility = View.GONE
//            emptyImage.visibility = View.VISIBLE
//            emptyTitle.visibility = View.VISIBLE
//            emptySubtitle.visibility = View.VISIBLE
//        }
//    }
//
//    //Fitur Add task
//    private fun showAddTaskDialog() {
//        // Inflate layout dialog_add_task
//        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
//        val dialog = AlertDialog.Builder(this)
//            .setView(dialogView)
//            .create()
//
//        // Inisialisasi komponen dalam dialog
//        val editTaskTitle = dialogView.findViewById<EditText>(R.id.edit_task_title)
//        val editTaskDescription = dialogView.findViewById<EditText>(R.id.edit_task_description)
//        val btnSubmitTask = dialogView.findViewById<Button>(R.id.btn_submit_task)
//
//        btnSubmitTask.setOnClickListener {
//            val title = editTaskTitle.text.toString()
//            val description = editTaskDescription.text.toString()
//
//            if (title.isNotEmpty()) {
//                // Tambahkan task ke dalam daftar
//                taskList.add(Task(title, description))
//                taskAdapter.notifyItemInserted(taskList.size - 1)
//                dialog.dismiss() // Tutup dialog
//            } else {
//                editTaskTitle.error = "Task title cannot be empty"
//            }
//        }
//
//        // Tampilkan dialog
//        dialog.show()
//    }
//}