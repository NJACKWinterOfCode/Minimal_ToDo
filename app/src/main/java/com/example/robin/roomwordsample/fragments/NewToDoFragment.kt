package com.example.robin.roomwordsample.fragments


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.robin.roomwordsample.Data.Word
import com.example.robin.roomwordsample.Data.WordViewModel
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.Utils.notify
import com.example.robin.roomwordsample.Utils.utils
import com.example.robin.roomwordsample.databinding.FragmentNewToDoBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class NewToDoFragment : Fragment() {

    var checked: Boolean = false
    var year = 0
    var month = 0
    var day = 0
    var hr = 0
    var min = 0
    private lateinit var wordViewModel: WordViewModel
    lateinit var colors: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        colors = resources.getStringArray(R.array.colors)
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentNewToDoBinding>(
            inflater,
            R.layout.fragment_new_to_do, container, false
        )

        val cross = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_cancel) }
        cross?.setColorFilter(
            ContextCompat.getColor(this.context!!, R.color.icons),
            PorterDuff.Mode.SRC_ATOP
        )
        if (activity?.actionBar != null) {
            activity?.actionBar?.elevation = 0F
            activity?.actionBar?.setDisplayShowTitleEnabled(false)
            activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
            activity?.actionBar?.setHomeAsUpIndicator(cross)
        }

        binding.userToDoEditText.requestFocus()


        wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
        binding.HasRemind.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                binding.EnterDateTime.visibility = View.INVISIBLE
                checked = false
            } else {
                binding.EnterDateTime.visibility = View.VISIBLE
                checked = true
            }
        }

        binding.EnterTime.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _, i, i1 ->
                    binding.EnterTime.setText("$i:$i1")
                    hr = i
                    min = i1
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.EnterDate.setOnClickListener {
            val mcurrentDate = Calendar.getInstance()
            val mYear = mcurrentDate.get(Calendar.YEAR)
            val mMonth = mcurrentDate.get(Calendar.MONTH)
            val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            val myFormat = "dd MMM, yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val mDatePicker = DatePickerDialog(
                this.context!!,
                DatePickerDialog.OnDateSetListener { _, selectedyear, selectedmonth, selectedday ->
                    binding.EnterDate.setText(selectedday.toString() + "/" + (selectedmonth + 1) + "/" + selectedyear)
                    year = selectedyear
                    month = selectedmonth
                    day = selectedday
                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        binding.makeToDoFloatingActionButton.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.userToDoEditText.text)) {
                val snackbar = Snackbar.make(
                    binding.ParentLayout,
                    "Task field cannot be empty",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            } else {
                val task = binding.userToDoEditText.text.toString()
                val description = binding.descriptionEditText.text.toString()
                var tag = "tag"
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.US)
                val tm = formatter.format(Date(System.currentTimeMillis()))
//                replyIntent.putExtra(NewWordActivity.EXTRA_REPLY, task)
                replyIntent.putExtra("Time", tm)
                if (checked) {
                    val taskName = binding.userToDoEditText.text.toString()
                    wordViewModel.addTask(taskName)

                    val c = Calendar.getInstance()
                    c.set(year, month, day, hr, min)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    val notifyManager = OneTimeWorkRequest.Builder(notify::class.java)
                        .setInitialDelay(
                            c.timeInMillis - System.currentTimeMillis(),
                            TimeUnit.MILLISECONDS
                        )
                        .addTag(c.timeInMillis.toString())
                        .build()
                    tag = c.timeInMillis.toString()
                    WorkManager.getInstance().enqueue(notifyManager)
                }
                replyIntent.putExtra("tag", tag)
                wordViewModel.insert(
                    Word(
                        task,
                        tm,
                        tag,
                        false,
                        description,
                        colors[Random.nextInt(0, colors.size - 1)]
                    )
                )
                fragmentManager?.popBackStack()
                utils.closeKeyboard(context!!)

            }
        }
        return binding.root
    }
  
  


}
