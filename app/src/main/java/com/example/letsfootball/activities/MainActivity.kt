package com.example.letsfootball.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsfootball.Adapters.FixersAdpater
import com.example.letsfootball.Adapters.adapter
import com.example.letsfootball.Adapters.liveadapter
import com.example.letsfootball.Adapters.pointsadapter
import com.example.letsfootball.Adapters.ptadapater
import com.example.letsfootball.Model.Fixture1model
import com.example.letsfootball.Model.Fixture2model
import com.example.letsfootball.Model.Score
import com.example.letsfootball.Model.awaylive
import com.example.letsfootball.Model.commencemodel
import com.example.letsfootball.Model.completelive
import com.example.letsfootball.Model.dataclass
import com.example.letsfootball.Model.datemodel
import com.example.letsfootball.Model.homelive
import com.example.letsfootball.Model.pointsdata
import com.example.letsfootball.Model.ptdata
import com.example.letsfootball.Model.scoreslive
import com.example.letsfootball.Model.timemodel
import com.example.letsfootball.Model.updatelive
import com.example.letsfootball.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityMainBinding

    private lateinit var liveadapter: liveadapter
    private lateinit var homelist : MutableList<homelive>
    private lateinit var scorelis : MutableList<scoreslive>
    private lateinit var awaylist : MutableList<awaylive>
    private lateinit var updatelis: MutableList<updatelive>
    private lateinit var commencelist : MutableList<commencemodel>
    private lateinit var completelivelist : MutableList<completelive>


    private  lateinit var pointadapter : pointsadapter
    private  lateinit var plist : MutableList<pointsdata>

    private lateinit var adapter: adapter
    private lateinit var mlist: MutableList<dataclass>

    private lateinit var ptadapater: ptadapater
    private lateinit var ptlist: MutableList<ptdata>

    private lateinit var fixadapater: FixersAdpater
    private lateinit var fixturelist1: MutableList<Fixture1model>
    private lateinit var fixturelist2: MutableList<Fixture2model>
    private lateinit var scorelist: MutableList<Score>
    private lateinit var timelist : MutableList<timemodel>
    private lateinit var datelist : MutableList<datemodel>
    private var apikey= "53569b9b172b4969842e1e1afc9c35fb"
    private var apikey1oddsapi= "d81d3752cc030e7be94da2d81051f3c5"
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initia()

        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...") // Set your message here
        progressDialog?.setCancelable(false)
        //val key = "8qNVW0HEv4Qln3mp7zFQ36zEK8WsMgkn98pVKJN02CPewk4Cls3e1vYqdoyW"
        val database = FirebaseDatabase.getInstance()

        val premierLeagueRef = database.getReference("premier league").child("names")
        val premierLeaguewdlfa = database.getReference("premier league").child("wdlafa")
        val premierleaguepoint = database.getReference("premier league").child("Point")

        val premierLeaguewdlfixteres1 = database.getReference("premier league").child("fixtures1")
        val premierLeaguewdlfixteres2 = database.getReference("premier league").child("fixtures2")
        val premierLeaguewdlfixteresscore = database.getReference("premier league").child("score")
        val premierLeaguetime = database.getReference("premier league").child("time")
        val premierLeaguedate = database.getReference("premier league").child("dates")

        val laligalivehome= FirebaseDatabase.getInstance().getReference("laliga").child("livehome")
        val laligalivescore= FirebaseDatabase.getInstance().getReference("laliga").child("livescore")
        val laligaliveaway= FirebaseDatabase.getInstance().getReference("laliga").child("liveaway")
        val laligaliveupdate= FirebaseDatabase.getInstance().getReference("laliga").child("liveupdate")
        val laligalivecommnce =FirebaseDatabase.getInstance().getReference("laliga").child("commencetime")
        val laligalivecomplete =FirebaseDatabase.getInstance().getReference("laliga").child("complete")
        changecase()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("https://api.the-odds-api.com/v4/sports/soccer_spain_la_liga/scores?apiKey=d81d3752cc030e7be94da2d81051f3c5&daysFrom=1")
                    .get()
                    .build()
                val hometeam = mutableListOf<String>()
                val awayteam = mutableListOf<String>()
                val scores = mutableListOf<String>()
                val lastupdate = mutableListOf<String>()
                val commence = mutableListOf<String>()
                val complete = mutableListOf<String>()

                homelist.clear()
                scorelis.clear()
                awaylist.clear()
                updatelis.clear()
                commencelist.clear()
                completelivelist.clear()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val jsonString = response.body?.string()
                        val jsonArray = JSONArray(jsonString)

                        // Clear the lists before populating them with new data
                        hometeam.clear()
                        awayteam.clear()
                        scores.clear()
                        lastupdate.clear()
                        commence.clear()
                        complete.clear()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val homeTeam = jsonObject.getString("home_team")
                            val awayTeam = jsonObject.getString("away_team")
                            val scoreslive = jsonObject.getString("scores")
                            val update = jsonObject.getString("last_update")
                            val commencee = jsonObject.getString("commence_time")
                            val comp = jsonObject.getString("completed")
                            // Add the data to the lists
                            hometeam.add(homeTeam)
                            awayteam.add(awayTeam)
                            scores.add(scoreslive)
                            lastupdate.add(update)
                            commence.add(commencee)
                            complete.add(comp)
                        }
                        val scoreList = mutableListOf<String>()
                        val otherDataList = mutableListOf<String>()
                        val onlyscoresonly = mutableListOf<String>()

                        onlyscoresonly.clear()
                        scoreList.clear()
                        otherDataList.clear()
                        for (score in scores) {
                            if (score != "null") {
                                onlyscoresonly.clear()
                                val onlyscore = JSONArray(score)
                                for (i in 0 until onlyscore.length()){
                                    val jsonObject = onlyscore.getJSONObject(i)
                                    val scoresonly = jsonObject.getString("score")
                                    onlyscoresonly.add(scoresonly)
                                }

                                scoreList.add(onlyscoresonly.toString())
                            } else {
                                // Add the "NULL" score to the otherDataList
                                otherDataList.add(score)
                            }
                        }
                        Log.d("scoring",scoreList.toString())
                        laligalivehome.setValue(hometeam)
                        laligalivescore.setValue(scoreList+otherDataList)
                        laligaliveaway.setValue(awayteam)
                        laligaliveupdate.setValue(lastupdate)
                        laligalivecommnce.setValue(commence)
                        laligalivecomplete.setValue(complete)
                        Log.d("away team",awayteam.toString())
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                // Connect to the website and fetch the HTML
                val Premierleague =
                    Jsoup.connect("https://www.skysports.com/premier-league-table").get()

                // Extract the team names
                val teamElements =
                    Premierleague.select(".standing-table__cell, .standing-table__cell--name")
                val wdlfa = Premierleague
                    .select(".is-hidden--bp35:not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")

                val ptpoint =
                    Premierleague.select(".standing-table__cell:not(.standing-table__cell:first-child):not(.is-hidden--bp35):not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")

                // Iterate over the team elements and extract the text (team names)
                val teamNames = mutableListOf<String>()
                for (element in teamElements) {
                    val longName = element.attr("data-long-name")
                    if (longName.isNotEmpty()) {
                        teamNames.add(longName)
                    }
                }

                // Iterate over the team points elements and extract the text (team points)
                val teamPoints = mutableListOf<String>()
                for (pointElement in wdlfa) {
                    val points = pointElement.text()
                    if (points.isNotEmpty()) {
                        teamPoints.add(points)
                    }
                }
                val pPoints = mutableListOf<String>()
                for (elements in ptpoint) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        pPoints.add(points)
                    }
                }


                val Premierleaguefix =
                    Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()
                val fixside1 = Premierleaguefix
                    .select(".matches__participant--side1")
                val fixside2 = Premierleaguefix
                    .select(".matches__participant--side2")
                val fix_TIME = Premierleaguefix.select(".matches__date")

                val scoreboard = Premierleaguefix.select(".matches__teamscores")
                val dateofmatch = Premierleaguefix.select(".fixres__header2")


                val dateofm = mutableListOf<String>()
                for (elements in dateofmatch) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        dateofm.add(points)
                    }
                }
                Log.d("dateitma", dateofm.toString())


                val score = mutableListOf<String>()
                for (elements in scoreboard) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        score.add(points)
                    }
                }
                Log.d("score", score.toString())

                val fix1 = mutableListOf<String>()
                for (elements in fixside1) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        fix1.add(points)
                    }
                }
                val fix2 = mutableListOf<String>()
                for (elements in fixside2) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        fix2.add(points)
                    }
                }
                val time = mutableListOf<String>()
                for (elements in fix_TIME) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        time.add(points)
                    }
                }
                val doc = Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()

                val fixtureBodies = doc.select(".fixres__header2,.fixres__item")

                val classNames = mutableListOf<String>()
                val countValues = mutableListOf<String>()
                var count = 0

                for (element in fixtureBodies) {
                    val className = element.attr("class")
                    if (className.isNotEmpty()) {
                        classNames.add(className)
                        if (className == "fixres__header2") {
                            // If the class is fixres__header2, print the count
                            if (count > 0) {
                                Log.d("Count between header 2 elements", count.toString())
                                countValues.add(count.toString())
                            }
                            count = 0 // Reset count for the next header 2
                        } else {
                            count++
                        }
                    }
                }

                // Store the count value for the last set of items if count is not 0
                if (count > 0) {
                    countValues.add(count.toString())
                }


                val repeatedDates = mutableListOf<String>()

                for (i in countValues.indices) {
                    val count = countValues[i].toInt()

                    // Check if the count is greater than 1
                    if (count > 1) {
                        // Repeat the date count times
                        for (j in 0 until count) {
                            repeatedDates.add(dateofm[i])
                        }
                    } else if (count == 1) {
                        // If count is 1, add the date only once
                        repeatedDates.add(dateofm[i])
                    }
                    // Skip adding the date if count is 0
                }

                Log.d("ttttt", repeatedDates.toString())

                premierLeagueRef.setValue(teamNames)
                premierLeaguewdlfa.setValue(teamPoints)
                premierleaguepoint.setValue(pPoints)
                premierLeaguewdlfixteres1.setValue(fix1)
                premierLeaguewdlfixteres2.setValue(fix2)
                premierLeaguewdlfixteresscore.setValue(score)
                premierLeaguetime.setValue(time)
                premierLeaguedate.setValue(repeatedDates)


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        premierLeagueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                for (snapshot in snapshot.children) {

                    val todotest = snapshot.getValue()?.let {
                        dataclass(it, snapshot.value.toString())
                    }
                    if (todotest != null) {
                        mlist.add(todotest)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        premierLeaguewdlfa.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                plist.clear()
                for (snapshot in snapshot.children) {
                    val points = snapshot.getValue()?.let {
                        pointsdata(snapshot.getValue().toString())
                    }
                    if (points != null) {
                        plist.add(points)
                    }

                }
                pointadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        premierleaguepoint.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ptlist.clear()
                for (snapshot in snapshot.children) {
                    val points = snapshot.getValue()?.let {
                        ptdata(snapshot.getValue().toString())
                    }
                    if (points != null) {
                        ptlist.add(points)
                    }

                }
                ptadapater.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        Log.d("hahaha", plist.toString())

        premierLeaguewdlfixteres1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fixturelist1.clear()
                for (snapshot in snapshot.children) {
                    val points = snapshot.getValue()?.let {
                        Fixture1model(fix1 = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        fixturelist1.add(points)
                    }
                }
                // Notify the adapter of the data change
                fixadapater.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        premierLeaguewdlfixteres2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fixturelist2.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        Fixture2model(fix2 = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        fixturelist2.add(points)
                    }
                }

                fixadapater.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        premierLeaguewdlfixteresscore.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scorelist.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        Score(scores = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        scorelist.add(points)
                    }
                }
                fixadapater.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        premierLeaguetime.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                timelist.clear()

                // Define the format for parsing the time (24-hour format)
                val gmtFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                gmtFormat24.timeZone = TimeZone.getTimeZone("GMT")

                // Define the format for formatting the time (12-hour format)
                val localFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())

                for (snapshot in snapshot.children) {
                    val gmtTime = snapshot.getValue(String::class.java)
                    if (gmtTime != null) {
                        try {
                            // Parse the GMT time string using the 24-hour format
                            val gmtDate = gmtFormat24.parse(gmtTime)

                            // Format the local time using the 12-hour format
                            val localTime = localFormat12.format(gmtDate)

                            // Add the local time to the list
                            val points = timemodel(matchtime = localTime)
                            timelist.add(points)
                        } catch (e: ParseException) {
                            // Handle parsing errors
                            e.printStackTrace()
                        }
                    }
                }
                fixadapater.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        premierLeaguedate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datelist.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        datemodel(matchdate = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        datelist.add(points)
                    }
                }
                fixadapater.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        laligalivehome.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                homelist.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        homelive(hometeam = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        homelist.add(points)
                    }
                }
                liveadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        laligalivescore.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scorelis.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                       scoreslive(score = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        scorelis.add(points)
                    }
                }
                liveadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        laligaliveaway.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                awaylist.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        awaylive(awayteam = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        awaylist.add(points)
                    }
                }
                liveadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        laligaliveupdate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updatelis.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                       updatelive(updateliv = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        updatelis.add(points)
                    }
                }
                liveadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        laligalivecommnce.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               commencelist.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        commencemodel(commencetime = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        commencelist.add(points)
                    }
                }
                liveadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        laligalivecomplete.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               completelivelist.clear()
                for (snapshot in snapshot.children) {

                    val points = snapshot.getValue()?.let {
                        completelive(complete = snapshot.getValue().toString())
                    }
                    if (points != null) {
                        completelivelist.add(points)
                    }
                }
                liveadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




    private fun changecase() {
        val database = FirebaseDatabase.getInstance()

        val premierLeagueRef = database.getReference("premier league").child("names")
        val premierLeaguewdlfa = database.getReference("premier league").child("wdlafa")
        val premierleaguepoint = database.getReference("premier league").child("Point")
        val premierLeaguewdlfixteres1 = database.getReference("premier league").child("fixtures1")
        val premierLeaguewdlfixteres2 = database.getReference("premier league").child("fixtures2")
        val premierLeaguewdlfixteresscore = database.getReference("premier league").child("score")
        val premierLeaguetime = database.getReference("premier league").child("time")
        val premierLeaguedate = database.getReference("premier league").child("dates")

        val laligaref= database.getReference("laliga").child("names");
        val Laligawdlafa = database.getReference("laliga").child("wdlafa")
        val laligaepoint = database.getReference("laliga").child("Point")
        val laligafixteres1 = database.getReference("laliga").child("fixtures1")
        val laligafixteres2 = database.getReference("laliga").child("fixtures2")
        val laligafixteresscore = database.getReference("laliga").child("score")
        val laligatime = database.getReference("laliga").child("time")
        val laligadate = database.getReference("laliga").child("dates")

        val ligue1 = database.getReference("ligue1").child("name")
        val ligue1awdlafa = database.getReference("ligue1").child("wdlafa")
        val ligue1point = database.getReference("ligue1").child("Point")
        val ligue1fixteres1 = database.getReference("ligue1").child("fixtures1")
        val ligue1fixteres2 = database.getReference("ligue1").child("fixtures2")
        val ligue1fixteresscore = database.getReference("ligue1").child("score")
        val ligue1time = database.getReference("ligue1").child("time")
        val ligue1date = database.getReference("ligue1").child("dates")

        val premierLeaguehome= FirebaseDatabase.getInstance().getReference("premier league").child("livehome")
        val premierLeaguescore= FirebaseDatabase.getInstance().getReference("premier league").child("livescore")
        val premierLeagueaway= FirebaseDatabase.getInstance().getReference("premier league").child("liveaway")
        val premierLeagueupdate= FirebaseDatabase.getInstance().getReference("premier league").child("liveupdate")
        val premierLeaguecommnce =FirebaseDatabase.getInstance().getReference("premier league").child("commencetime")
        val premierLeaguecomplete =FirebaseDatabase.getInstance().getReference("premier league").child("complete")

        val laligalivehome= FirebaseDatabase.getInstance().getReference("laliga").child("livehome")
        val laligalivescore= FirebaseDatabase.getInstance().getReference("laliga").child("livescore")
        val laligaliveaway= FirebaseDatabase.getInstance().getReference("laliga").child("liveaway")
        val laligaliveupdate= FirebaseDatabase.getInstance().getReference("laliga").child("liveupdate")
        val laligalivecommnce =FirebaseDatabase.getInstance().getReference("laliga").child("commencetime")
        val laligalivecomplete =FirebaseDatabase.getInstance().getReference("laliga").child("complete")

        val ligue1home= FirebaseDatabase.getInstance().getReference("laliga").child("livehome")
        val ligue1score= FirebaseDatabase.getInstance().getReference("laliga").child("livescore")
        val ligue1away= FirebaseDatabase.getInstance().getReference("laliga").child("liveaway")
        val ligue1update= FirebaseDatabase.getInstance().getReference("laliga").child("liveupdate")
        val ligue1commnce =FirebaseDatabase.getInstance().getReference("laliga").child("commencetime")
        val ligue1complete =FirebaseDatabase.getInstance().getReference("laliga").child("complete")

        binding.premier.setOnClickListener {

            // Make the TextView visible
            binding.prem.visibility = View.VISIBLE
            binding.lalig.visibility = View.GONE
            binding.ligue.visibility = View.GONE

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://api.the-odds-api.com/v4/sports/soccer_spain_la_liga/scores?apiKey=d81d3752cc030e7be94da2d81051f3c5&daysFrom=1")
                        .get()
                        .build()
                    val hometeam = mutableListOf<String>()
                    val awayteam = mutableListOf<String>()
                    val scores = mutableListOf<String>()
                    val lastupdate = mutableListOf<String>()
                    val commence = mutableListOf<String>()
                    val complete = mutableListOf<String>()

                    homelist.clear()
                    scorelis.clear()
                    awaylist.clear()
                    updatelis.clear()
                    commencelist.clear()
                    completelivelist.clear()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val jsonString = response.body?.string()
                            val jsonArray = JSONArray(jsonString)

                            // Clear the lists before populating them with new data
                            hometeam.clear()
                            awayteam.clear()
                            scores.clear()
                            lastupdate.clear()
                            commence.clear()
                            complete.clear()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val homeTeam = jsonObject.getString("home_team")
                                val awayTeam = jsonObject.getString("away_team")
                                val scoreslive = jsonObject.getString("scores")
                                val update = jsonObject.getString("last_update")
                                val commencee = jsonObject.getString("commence_time")
                                val comp = jsonObject.getString("completed")
                                // Add the data to the lists
                                hometeam.add(homeTeam)
                                awayteam.add(awayTeam)
                                scores.add(scoreslive)
                                lastupdate.add(update)
                                commence.add(commencee)
                                complete.add(comp)
                            }
                            val scoreList = mutableListOf<String>()
                            val otherDataList = mutableListOf<String>()
                            val onlyscoresonly = mutableListOf<String>()

                            onlyscoresonly.clear()
                            scoreList.clear()
                            otherDataList.clear()
                            for (score in scores) {
                                if (score != "null") {
                                    onlyscoresonly.clear()
                                    val onlyscore = JSONArray(score)
                                    for (i in 0 until onlyscore.length()){
                                        val jsonObject = onlyscore.getJSONObject(i)
                                        val scoresonly = jsonObject.getString("score")
                                        onlyscoresonly.add(scoresonly)
                                    }

                                    scoreList.add(onlyscoresonly.toString())
                                } else {
                                    // Add the "NULL" score to the otherDataList
                                    otherDataList.add(score)
                                }
                            }
                            Log.d("scoring",scoreList.toString())
                            laligalivehome.setValue(hometeam)
                            laligalivescore.setValue(scoreList+otherDataList)
                            laligaliveaway.setValue(awayteam)
                            laligaliveupdate.setValue(lastupdate)
                            laligalivecommnce.setValue(commence)
                            laligalivecomplete.setValue(complete)
                            Log.d("away team",awayteam.toString())
                        }
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    // Connect to the website and fetch the HTML
                    val Premierleague = Jsoup.connect("https://www.skysports.com/premier-league-table").get()
                    Log.d("hahahah",Premierleague.toString())

                    // Extract the team names
                    val teamElements = Premierleague.select(".standing-table__cell, .standing-table__cell--name")
                    val wdlfa = Premierleague
                        .select(".is-hidden--bp35:not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")

                    val ptpoint = Premierleague.select(".standing-table__cell:not(.standing-table__cell:first-child):not(.is-hidden--bp35):not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")


                    // Iterate over the team elements and extract the text (team names)
                    val teamNames = mutableListOf<String>()
                    for (element in teamElements) {
                        val longName = element.attr("data-long-name")
                        if (longName.isNotEmpty()) {
                            teamNames.add(longName)
                        }
                    }

                    // Iterate over the team points elements and extract the text (team points)
                    val teamPoints = mutableListOf<String>()
                    for (pointElement in wdlfa) {
                        val points = pointElement.text()
                        if (points.isNotEmpty()) {
                            teamPoints.add(points)
                        }
                    }
                    val pPoints = mutableListOf<String>()
                    for (elements in ptpoint) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            pPoints.add(points)
                        }
                    }



                    val Premierleaguefix = Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()
                    val fixside1 = Premierleaguefix
                        .select(".matches__participant--side1")
                    val fixside2 = Premierleaguefix
                        .select(".matches__participant--side2")
                    val fix_TIME = Premierleaguefix.select(".matches__date")

                    val scoreboard = Premierleaguefix.select(".matches__teamscores")
                    val dateofmatch = Premierleaguefix.select(".fixres__header2")
                    val score = mutableListOf<String>()
                    for (elements in scoreboard) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            score.add(points)
                        }
                    }
                    Log.d("score",score.toString())

                    val fix1 = mutableListOf<String>()
                    for (elements in fixside1) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            fix1.add(points)
                        }
                    }
                    val fix2 = mutableListOf<String>()
                    for (elements in fixside2) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            fix2.add(points)
                        }
                    }
                    val time = mutableListOf<String>()
                    for (elements in fix_TIME) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            time.add(points)
                        }
                    }
                    val dateofm = mutableListOf<String>()
                    for (elements in dateofmatch) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            dateofm.add(points)
                        }
                    }
                    val doc = Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()

                    val fixtureBodies = doc.select(".fixres__header2,.fixres__item")

                    val classNames = mutableListOf<String>()
                    val countValues = mutableListOf<String>()
                    var count = 0

                    for (element in fixtureBodies) {
                        val className = element.attr("class")
                        if (className.isNotEmpty()) {
                            classNames.add(className)
                            if (className == "fixres__header2") {
                                // If the class is fixres__header2, print the count
                                if (count > 0) {
                                    Log.d("Count between header 2 elements", count.toString())
                                    countValues.add(count.toString())
                                }
                                count = 0 // Reset count for the next header 2
                            } else {
                                count++
                            }
                        }
                    }

                    // Store the count value for the last set of items if count is not 0
                    if (count > 0) {
                        countValues.add(count.toString())
                    }


                    val repeatedDates = mutableListOf<String>()

                    for (i in countValues.indices) {
                        val count = countValues[i].toInt()

                        // Check if the count is greater than 1
                        if (count > 1) {
                            // Repeat the date count times
                            for (j in 0 until count) {
                                repeatedDates.add(dateofm[i])
                            }
                        } else if (count == 1) {
                            // If count is 1, add the date only once
                            repeatedDates.add(dateofm[i])
                        }
                        // Skip adding the date if count is 0
                    }
                    premierLeagueRef.setValue(teamNames)
                    premierLeaguewdlfa.setValue(teamPoints)
                    premierleaguepoint.setValue(pPoints)
                    premierLeaguewdlfixteres1.setValue(fix1)
                    premierLeaguewdlfixteres2.setValue(fix2)
                    premierLeaguewdlfixteresscore.setValue(score)
                    premierLeaguetime.setValue(time)
                    premierLeaguedate.setValue(repeatedDates)




                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


            premierLeagueRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapter.currentValue = 1
                    mlist.clear()
                    for (snapshot in snapshot.children) {

                        val todotest = snapshot.getValue()?.let {
                            dataclass(it,snapshot.value.toString())
                        }
                        if (todotest != null) {
                            mlist.add(todotest)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            premierLeaguewdlfa.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    plist.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            pointsdata(snapshot.getValue().toString())
                        }
                        if (points != null) {
                            plist.add(points)
                        }

                    }
                    pointadapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            premierLeaguewdlfixteres1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fixturelist1.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            Fixture1model(fix1 = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            fixturelist1.add(points)
                        }
                    }
                    // Notify the adapter of the data change
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            premierLeaguewdlfixteres2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fixturelist2.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            Fixture2model(fix2 = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            fixturelist2.add(points)
                        }
                    }

                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            premierLeaguewdlfixteresscore.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scorelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            Score(scores = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            scorelist.add(points)
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            premierleaguepoint.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ptlist.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            ptdata(snapshot.getValue().toString())
                        }
                        if (points != null) {
                            ptlist.add(points)
                        }

                    }
                    ptadapater.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            premierLeaguetime.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    timelist.clear()

                    // Define the format for parsing the time (24-hour format)
                    val gmtFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                    gmtFormat24.timeZone = TimeZone.getTimeZone("GMT")

                    // Define the format for formatting the time (12-hour format)
                    val localFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    for (snapshot in snapshot.children) {
                        val gmtTime = snapshot.getValue(String::class.java)
                        if (gmtTime != null) {
                            try {
                                // Parse the GMT time string using the 24-hour format
                                val gmtDate = gmtFormat24.parse(gmtTime)

                                // Format the local time using the 12-hour format
                                val localTime = localFormat12.format(gmtDate)

                                // Add the local time to the list
                                val points = timemodel(matchtime = localTime)
                                timelist.add(points)
                            } catch (e: ParseException) {
                                // Handle parsing errors
                                e.printStackTrace()
                            }
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            premierLeaguedate.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    datelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            datemodel(matchdate = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            datelist.add(points)
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.laliga.setOnClickListener {

            // Make the TextView visible
            binding.lalig.visibility = View.VISIBLE
            binding.prem.visibility = View.GONE
            binding.ligue.visibility = View.GONE
            progressDialog?.show()
            GlobalScope.launch(Dispatchers.IO) {
                val laliga = Jsoup.connect("https://www.skysports.com/la-liga-table").get()
                val teamElements =
                    laliga.select(".standing-table__cell, .standing-table__cell--name")
                val wdlfa = laliga
                    .select(".is-hidden--bp35:not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")

                val ptpoint =
                    laliga.select(".standing-table__cell:not(.standing-table__cell:first-child):not(.is-hidden--bp35):not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")


                // Iterate over the team elements and extract the text (team names)
                val teamNames = mutableListOf<String>()
                for (element in teamElements) {
                    val longName = element.attr("data-long-name")
                    if (longName.isNotEmpty()) {
                        teamNames.add(longName)
                    }
                }

                // Iterate over the team points elements and extract the text (team points)
                val teamPoints = mutableListOf<String>()
                for (pointElement in wdlfa) {
                    val points = pointElement.text()
                    if (points.isNotEmpty()) {
                        teamPoints.add(points)
                    }
                }
                val pPoints = mutableListOf<String>()
                for (elements in ptpoint) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        pPoints.add(points)
                    }
                }

                val laligafix = Jsoup.connect("https://www.skysports.com/la-liga-fixtures").get()
                val fixside1 = laligafix
                    .select(".matches__participant--side1")
                val fixside2 = laligafix
                    .select(".matches__participant--side2")
                val fix_TIME = laligafix.select(".matches__date")

                val scoreboard = laligafix.select(".matches__teamscores")
                val dateofmatch = laligafix.select(".fixres__header2")
                val score = mutableListOf<String>()
                for (elements in scoreboard) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        score.add(points)
                    }
                }
                Log.d("score", score.toString())

                val fix1 = mutableListOf<String>()
                for (elements in fixside1) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        fix1.add(points)
                    }
                }
                val fix2 = mutableListOf<String>()
                for (elements in fixside2) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        fix2.add(points)
                    }
                }
                val time = mutableListOf<String>()
                for (elements in fix_TIME) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        time.add(points)
                    }
                }
                val dateofm = mutableListOf<String>()
                for (elements in dateofmatch) {
                    val points = elements.text()
                    if (points.isNotEmpty()) {
                        dateofm.add(points)
                    }
                }
                val doc = Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()

                val fixtureBodies = doc.select(".fixres__header2,.fixres__item")

                val classNames = mutableListOf<String>()
                val countValues = mutableListOf<String>()
                var count = 0

                for (element in fixtureBodies) {
                    val className = element.attr("class")
                    if (className.isNotEmpty()) {
                        classNames.add(className)
                        if (className == "fixres__header2") {
                            // If the class is fixres__header2, print the count
                            if (count > 0) {
                                Log.d("Count between header 2 elements", count.toString())
                                countValues.add(count.toString())
                            }
                            count = 0 // Reset count for the next header 2
                        } else {
                            count++
                        }
                    }
                }

                // Store the count value for the last set of items if count is not 0
                if (count > 0) {
                    countValues.add(count.toString())
                }


                val repeatedDates = mutableListOf<String>()

                for (i in countValues.indices) {
                    val count = countValues[i].toInt()

                    // Check if the count is greater than 1
                    if (count > 1) {
                        // Repeat the date count times
                        for (j in 0 until count) {
                            repeatedDates.add(dateofm[i])
                        }
                    } else if (count == 1) {
                        // If count is 1, add the date only once
                        repeatedDates.add(dateofm[i])
                    }
                    Log.d("laliga", teamNames.toString())

                    val laligaref =
                        FirebaseDatabase.getInstance().getReference("laliga").child("names");
                    laligaref.setValue(teamNames)
                    Laligawdlafa.setValue(teamPoints)
                    laligaepoint.setValue(pPoints)
                    laligafixteres1.setValue(fix1)
                    laligafixteres2.setValue(fix2)
                    laligafixteresscore.setValue(score)
                    laligatime.setValue(time)
                    laligadate.setValue(repeatedDates)

                }
            }

            laligaref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapter.currentValue = 1
                    mlist.clear()
                    for (snapshot in snapshot.children) {

                        val todotest = snapshot.getValue()?.let {
                            dataclass(it,snapshot.value.toString())
                        }
                        if (todotest != null) {
                            mlist.add(todotest)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            Laligawdlafa.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    plist.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            pointsdata(snapshot.getValue().toString())
                        }
                        if (points != null) {
                            plist.add(points)
                        }

                    }
                    pointadapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligafixteres1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fixturelist1.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            Fixture1model(fix1 = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            fixturelist1.add(points)
                        }
                    }
                    // Notify the adapter of the data change
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            laligafixteres2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fixturelist2.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            Fixture2model(fix2 = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            fixturelist2.add(points)
                        }
                    }

                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            laligafixteresscore.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scorelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            Score(scores = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            scorelist.add(points)
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligaepoint.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ptlist.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            ptdata(snapshot.getValue().toString())
                        }
                        if (points != null) {
                            ptlist.add(points)
                        }

                    }
                    ptadapater.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligatime.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    timelist.clear()

                    // Define the format for parsing the time (24-hour format)
                    val gmtFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                    gmtFormat24.timeZone = TimeZone.getTimeZone("GMT")

                    // Define the format for formatting the time (12-hour format)
                    val localFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    for (snapshot in snapshot.children) {
                        val gmtTime = snapshot.getValue(String::class.java)
                        if (gmtTime != null) {
                            try {
                                // Parse the GMT time string using the 24-hour format
                                val gmtDate = gmtFormat24.parse(gmtTime)

                                // Format the local time using the 12-hour format
                                val localTime = localFormat12.format(gmtDate)

                                // Add the local time to the list
                                val points = timemodel(matchtime = localTime)
                                timelist.add(points)
                            } catch (e: ParseException) {
                                // Handle parsing errors
                                e.printStackTrace()
                            }
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligadate.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    datelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            datemodel(matchdate = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            datelist.add(points)
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                    progressDialog?.hide()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligalivehome.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    homelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            homelive(hometeam = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            homelist.add(points)
                        }
                    }
                    liveadapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligalivescore.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scorelis.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            scoreslive(score = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            scorelis.add(points)
                        }
                    }
                    liveadapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligaliveaway.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    awaylist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            awaylive(awayteam = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            awaylist.add(points)
                        }
                    }
                    liveadapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligaliveupdate.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    updatelis.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            updatelive(updateliv = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            updatelis.add(points)
                        }
                    }
                    liveadapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            laligalivecommnce.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commencelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            commencemodel(commencetime = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            commencelist.add(points)
                        }
                    }
                    liveadapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            laligalivecomplete.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    completelivelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            completelive(complete = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            completelivelist.add(points)
                        }
                    }
                    liveadapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.ligue1.setOnClickListener{

            binding.ligue.visibility = View.VISIBLE
            binding.lalig.visibility = View.GONE
            binding.prem.visibility = View.GONE

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    // Connect to the website and fetch the HTML
                    val ligue = Jsoup.connect("https://www.skysports.com/ligue-1-table").get()

                    // Extract the team names
                    val teamElements = ligue.select(".standing-table__cell, .standing-table__cell--name")
                    val wdlfa = ligue
                        .select(".is-hidden--bp35:not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")

                    val ptpoint = ligue.select(".standing-table__cell:not(.standing-table__cell:first-child):not(.is-hidden--bp35):not(.is-hidden--bp15):not(.standing-table__header-cell):not(.standing-table__cell--name)")


                    // Iterate over the team elements and extract the text (team names)
                    val teamNames = mutableListOf<String>()
                    for (element in teamElements) {
                        val longName = element.attr("data-long-name")
                        if (longName.isNotEmpty()) {
                            teamNames.add(longName)
                        }
                    }

                    // Iterate over the team points elements and extract the text (team points)
                    val teamPoints = mutableListOf<String>()
                    for (pointElement in wdlfa) {
                        val points = pointElement.text()
                        if (points.isNotEmpty()) {
                            teamPoints.add(points)
                        }
                    }
                    val pPoints = mutableListOf<String>()
                    for (elements in ptpoint) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            pPoints.add(points)
                        }
                    }



                    val liguefix = Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()
                    val fixside1 =  liguefix
                        .select(".matches__participant--side1")
                    val fixside2 =  liguefix
                        .select(".matches__participant--side2")
                    val fix_TIME =  liguefix.select(".matches__date")

                    val scoreboard =  liguefix.select(".matches__teamscores")
                    val dateofmatch = liguefix.select(".fixres__header2")
                    val score = mutableListOf<String>()
                    for (elements in scoreboard) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            score.add(points)
                        }
                    }
                    Log.d("score",score.toString())

                    val fix1 = mutableListOf<String>()
                    for (elements in fixside1) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            fix1.add(points)
                        }
                    }
                    val fix2 = mutableListOf<String>()
                    for (elements in fixside2) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            fix2.add(points)
                        }
                    }
                    val time = mutableListOf<String>()
                    for (elements in fix_TIME) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            time.add(points)
                        }
                    }
                    val dateofm = mutableListOf<String>()
                    for (elements in dateofmatch) {
                        val points = elements.text()
                        if (points.isNotEmpty()) {
                            dateofm.add(points)
                        }
                    }
                    val doc = Jsoup.connect("https://www.skysports.com/premier-league-fixtures").get()

                    val fixtureBodies = doc.select(".fixres__header2,.fixres__item")

                    val classNames = mutableListOf<String>()
                    val countValues = mutableListOf<String>()
                    var count = 0

                    for (element in fixtureBodies) {
                        val className = element.attr("class")
                        if (className.isNotEmpty()) {
                            classNames.add(className)
                            if (className == "fixres__header2") {
                                // If the class is fixres__header2, print the count
                                if (count > 0) {
                                    Log.d("Count between header 2 elements", count.toString())
                                    countValues.add(count.toString())
                                }
                                count = 0 // Reset count for the next header 2
                            } else {
                                count++
                            }
                        }
                    }

                    // Store the count value for the last set of items if count is not 0
                    if (count > 0) {
                        countValues.add(count.toString())
                    }


                    val repeatedDates = mutableListOf<String>()

                    for (i in countValues.indices) {
                        val count = countValues[i].toInt()

                        // Check if the count is greater than 1
                        if (count > 1) {
                            // Repeat the date count times
                            for (j in 0 until count) {
                                repeatedDates.add(dateofm[i])
                            }
                        } else if (count == 1) {
                            // If count is 1, add the date only once
                            repeatedDates.add(dateofm[i])
                        }
                    }
                    Log.d("hello", fix1.toString())
                    Log.d("hello", time.toString())


                    ligue1.setValue(teamNames)
                    ligue1awdlafa.setValue(teamPoints)
                    ligue1point.setValue(pPoints)
                    ligue1fixteres1.setValue(fix1)
                    ligue1fixteres2.setValue(fix2)
                    ligue1fixteresscore.setValue(score)
                    ligue1time.setValue(time)
                    ligue1date.setValue(repeatedDates)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


            ligue1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapter.currentValue = 1
                    mlist.clear()
                    for (snapshot in snapshot.children) {

                        val todotest = snapshot.getValue()?.let {
                            dataclass(it,snapshot.value.toString())
                        }
                        if (todotest != null) {
                            mlist.add(todotest)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            ligue1awdlafa.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    plist.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            pointsdata(snapshot.getValue().toString())
                        }
                        if (points != null) {
                            plist.add(points)
                        }

                    }
                    pointadapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            ligue1fixteres1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fixturelist1.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            Fixture1model(fix1 = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            fixturelist1.add(points)
                        }
                    }
                    // Notify the adapter of the data change
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            ligue1fixteres2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fixturelist2.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            Fixture2model(fix2 = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            fixturelist2.add(points)
                        }
                    }

                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            ligue1fixteresscore.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scorelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            Score(scores = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            scorelist.add(points)
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            ligue1point.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ptlist.clear()
                    for (snapshot in snapshot.children) {
                        val points = snapshot.getValue()?.let {
                            ptdata(snapshot.getValue().toString())
                        }
                        if (points != null) {
                            ptlist.add(points)
                        }

                    }
                    ptadapater.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            ligue1time.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    timelist.clear()

                    // Define the format for parsing the time (24-hour format)
                    val gmtFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                    gmtFormat24.timeZone = TimeZone.getTimeZone("GMT")

                    // Define the format for formatting the time (12-hour format)
                    val localFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    for (snapshot in snapshot.children) {
                        val gmtTime = snapshot.getValue(String::class.java)
                        if (gmtTime != null) {
                            try {
                                // Parse the GMT time string using the 24-hour format
                                val gmtDate = gmtFormat24.parse(gmtTime)

                                // Format the local time using the 12-hour format
                                val localTime = localFormat12.format(gmtDate)

                                // Add the local time to the list
                                val points = timemodel(matchtime = localTime)
                                timelist.add(points)
                            } catch (e: ParseException) {
                                // Handle parsing errors
                                e.printStackTrace()
                            }
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            ligue1date.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    datelist.clear()
                    for (snapshot in snapshot.children) {

                        val points = snapshot.getValue()?.let {
                            datemodel(matchdate = snapshot.getValue().toString())
                        }
                        if (points != null) {
                            datelist.add(points)
                        }
                    }
                    fixadapater.notifyDataSetChanged()
                    progressDialog?.hide()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    private fun initia() {
        binding.Recyclevirew.setHasFixedSize(true)
        binding.Recyclevirew.layoutManager = LinearLayoutManager(this)
        mlist = mutableListOf()
        adapter = adapter(mlist)
        binding.Recyclevirew.adapter = adapter

        binding.livelistrc.setHasFixedSize(true)
        binding.livelistrc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        homelist = mutableListOf()
        scorelis = mutableListOf()
        awaylist = mutableListOf()
        updatelis = mutableListOf()
        commencelist = mutableListOf()
        completelivelist = mutableListOf()
        liveadapter = liveadapter(homelist,scorelis,awaylist,updatelis,commencelist,completelivelist)
        binding.livelistrc.adapter = liveadapter

        binding.Recyeclepoint.setHasFixedSize(true)
        binding.Recyeclepoint.layoutManager = GridLayoutManager(this, 5)
        plist = mutableListOf()
        pointadapter = pointsadapter(plist)
        binding.Recyeclepoint.adapter = pointadapter

        binding.ptrec.setHasFixedSize(true)
        binding.ptrec.layoutManager = GridLayoutManager(this, 3)
        ptlist = mutableListOf()
        ptadapater = ptadapater(ptlist)
        binding.ptrec.adapter = ptadapater

        binding.fixtures.setHasFixedSize(true)
        binding.fixtures.layoutManager = LinearLayoutManager(this)
        fixturelist1 = mutableListOf()
        fixturelist2 = mutableListOf()
        scorelist = mutableListOf()
        timelist = mutableListOf()
        datelist = mutableListOf()

        fixadapater = FixersAdpater(fixturelist1,fixturelist2,scorelist,timelist,datelist)
        binding.fixtures.adapter = fixadapater
    }
}