package com.example.homework11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView coursesRV;
    private static final int ADD_COURSE_REQUEST = 1;
    private static final int EDIT_COURSE_REQUEST = 2;
    private ViewModal viewmodal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coursesRV = findViewById(R.id.idRVCourses);
        FloatingActionButton fab = findViewById(R.id.idFABAdd);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivityForResult(intent, ADD_COURSE_REQUEST);
            }
        });

        coursesRV.setLayoutManager(new LinearLayoutManager(this));
        coursesRV.setHasFixedSize(true);
        final CourseRVAdapter adapter = new CourseRVAdapter();
        coursesRV.setAdapter(adapter);
        viewmodal = ViewModelProviders.of(this).get(ViewModal.class);
        viewmodal.getAllCourses().observe(this, adapter::submitList);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewmodal.delete(adapter.getCourseAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Course deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(coursesRV);

        adapter.setOnItemClickListener(model -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra(MainActivity2.EXTRA_ID, model.getId());
            intent.putExtra(MainActivity2.EXTRA_COURSE_NAME, model.getCourseName());
            intent.putExtra(MainActivity2.EXTRA_DESCRIPTION, model.getCourseDescription());
            intent.putExtra(MainActivity2.EXTRA_DURATION, model.getCourseDuration());
            startActivityForResult(intent, EDIT_COURSE_REQUEST);

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_COURSE_REQUEST && resultCode == RESULT_OK) {
            String courseName = data.getStringExtra(MainActivity2.EXTRA_COURSE_NAME);
            String courseDescription = data.getStringExtra(MainActivity2.EXTRA_DESCRIPTION);
            String courseDuration = data.getStringExtra(MainActivity2.EXTRA_DURATION);
            CourseModal model = new CourseModal(courseName, courseDescription, courseDuration);
            viewmodal.insert(model);
            Toast.makeText(this, "Course saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_COURSE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(MainActivity2.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Course can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String courseName = data.getStringExtra(MainActivity2.EXTRA_COURSE_NAME);
            String courseDesc = data.getStringExtra(MainActivity2.EXTRA_DESCRIPTION);
            String courseDuration = data.getStringExtra(MainActivity2.EXTRA_DURATION);
            CourseModal model = new CourseModal(courseName, courseDesc, courseDuration);
            model.setId(id);
            viewmodal.update(model);
            Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Course not saved", Toast.LENGTH_SHORT).show();
        }

    }
}