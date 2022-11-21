package com.example.donttouchthis

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.apache.commons.lang3.math.NumberUtils.isNumber
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream

class WheelPairsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        setContentView(R.layout.activity_main)

        val expListView: ExpandableListView = findViewById(R.id.expListView)


        // Intent
        val wPath: String = intent.getStringExtra("wheelsFilePath").toString()
        val bPath: String = intent.getStringExtra("bringsFilePath").toString()

        val searchString: String =
            intent.getStringExtra("searchQ")?.trim()?.replace("\\s+".toRegex(), ",").toString()
        val searches: List<String> = searchString.split(",")
        if (searches[0] != "")
            setInfoText("Вы ввели: $searchString")

        // apacheXlsx
        val wheelsSheet: Sheet? = getSheet(wPath)
        val bearingSheet: Sheet? = getSheet(bPath)
        val numWheel: Int = getNumberOfRows(wheelsSheet)
        val numBearing: Int = getNumberOfRows(bearingSheet)

        // Array
        var wheelData = Array(numWheel) { Array(5) { " " } }
        var bearingData = Array(numBearing) { Array(2) { " " } }

        wheelData = setWheelsData(wheelData, numWheel, wheelsSheet)
        bearingData = setBearingsData(bearingData, numBearing, bearingSheet)

        wheelData = calcTotalRun(wheelData, bearingData, searches, numWheel, numBearing)

        val groups = prepareAdapter(wheelData, searches)

        val adapter = OuterLevelExpandableAdapter(applicationContext, groups)
        expListView.setAdapter(adapter)
    }

    private fun prepareAdapter(
        wheelData: Array<Array<String>>,
        searches: List<String>,
    ): List<Pair<String, List<Pair<String, List<String>>>>> {
        val groups = mutableListOf<Pair<String, MutableList<Pair<String, MutableList<String>>>>>()
        try {
            for (i in wheelData.indices) {
                val trainNumber = wheelData[i][0]
                val wheelPairNumber = wheelData[i][2]
                val wheelPairInfo = mutableListOf(wheelData[i][1], wheelData[i][3], wheelData[i][4])
                for (search in searches)
                    if (shouldShow(trainNumber, wheelData[i][4], search))
                        updateValue(groups, trainNumber, wheelPairNumber to wheelPairInfo)
            }
        } catch (e: Exception) {
            setInfoText(" $e \n Проверьте таблицы")
        }
        return groups
    }

    private fun updateValue(
        arr: MutableList<Pair<String, MutableList<Pair<String, MutableList<String>>>>>,
        trainNumber: String,
        wheelPairInfo: Pair<String, MutableList<String>>,
    ) {
        var flag = false
        arr.forEach {
            if (it.first.contains(trainNumber)) {
                flag = true
                it.second.add(wheelPairInfo)
            }
        }
        if (!flag)
            arr.add(trainNumber to mutableListOf(wheelPairInfo))
    }

    private fun shouldShow(el0: String, el4: String, search: String): Boolean {
        if (search == el0 || search.isBlank())
            return (el4.split(" ")[0].toDouble() > 2000000) || el4.contains("и")
        return false
    }

    private fun calcTotalRun(
        wheelData: Array<Array<String>>,
        bearingData: Array<Array<String>>,
        searches: List<String>,
        numWheel: Int,
        numBearing: Int,
    ): Array<Array<String>> {
        try {
            for (i in 0 until numWheel) {
                for (search: String in searches)
                    if (search == wheelData[i][0] || search.isEmpty())
                        for (j in 0 until numBearing)
                            if (bearingData[j][0].isNotEmpty() && wheelData[i][2].toDouble() == bearingData[j][0].toDouble())
                                if (isNumber(bearingData[j][1])) {
                                    if (bearingData[j][1].toDouble() != .0)
                                        wheelData[i][4] =
                                            (wheelData[i][3].toDouble() + bearingData[j][1].toDouble()).toString()
                                } else wheelData[i][4] = wheelData[i][3] + " и " + bearingData[j][1]
            }
            wheelData.sortBy { it[0] }
        } catch (e: Exception) {
            setInfoText(e.toString())
        }
        return wheelData
    }


    private fun setBearingsData(
        arr: Array<Array<String>>,
        num: Int,
        sheet: Sheet?,
    ): Array<Array<String>> {

        for (i in 0 until num) {
            val wpN: String = sheet?.getRow(i + 1)?.getCell(1).toString().replace(".0", "").trim()
            val run: String = sheet?.getRow(i + 1)?.getCell(3).toString().replace(".0", "").trim()
            arr[i][0] = wpN
            arr[i][1] = run
        }
        return arr
    }

    private fun setWheelsData(
        arr: Array<Array<String>>,
        num: Int,
        sheet: Sheet?,
    ): Array<Array<String>> {
        for (i in 0 until num) {
            val tN = sheet?.getRow(i + 1)?.getCell(1).toString().replace(".0", "").trim()
            val pos = sheet?.getRow(i + 1)?.getCell(4).toString().replace(".0", "").trim()
            val wpN = sheet?.getRow(i + 1)?.getCell(5).toString().replace(".0", "").trim()
            val run = sheet?.getRow(i + 1)?.getCell(12).toString().replace(".0", "").trim()

            arr[i][0] = tN
            arr[i][1] = pos
            arr[i][2] = wpN
            arr[i][3] = run
            arr[i][4] = arr[i][3]

        }
        return arr
    }

    private fun getSheet(path: String?): Sheet? {
        return try {
            val f = File(path)
            val fis = FileInputStream(f)
            val wb: Workbook = WorkbookFactory.create(fis)
            wb.getSheetAt(0)
        } catch (ex: Exception) {
            setInfoText("$ex")
            null
        }
    }

    private fun getNumberOfRows(sheet: Sheet?): Int =
        if (sheet == null) 0 else (sheet.physicalNumberOfRows - 1)

    private fun setInfoText(text: String) {
        val eText: TextView = findViewById(R.id.eTextView)
        eText.append("\n$text")
    }

}