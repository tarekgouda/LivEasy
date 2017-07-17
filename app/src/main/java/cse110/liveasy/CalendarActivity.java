package cse110.liveasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.app.Activity;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

/* SOURCES:

   The following sources were utilized for taking a profile photo:

   https://examples.javacodegeeks.com/android/core/widget/android-calendarview-example/
 */

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendar;
    Bundle extras;

    int year;
    int month;
    int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);

        //sets the main layout of the activity
        setContentView(R.layout.calendar);

        //initializes the calendarview
        initializeCalendar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void setTime(int day, int month, int year)
    {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay()
    {
        return day;
    }

    public void initializeCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new OnDateChangeListener() {
            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day)
            {

                setTime(day, month, year);
                setContentView(R.layout.add_event_popup);

                Spinner dropdown = (Spinner)findViewById(R.id.month_spinner);
                String[] items = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                        "Aug", "Sept", "Oct", "Nov", "Dec"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, items);
                dropdown.setAdapter(adapter);
            }
        });
    }

    public void goToCalendar( View view ) {

        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("username", extras.getString("username"));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent goBack = new Intent(this, NavDrawerActivity.class);
                goBack.putExtra("username", extras.getString("username"));
                startActivity(goBack);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        //do nothing
    }
}