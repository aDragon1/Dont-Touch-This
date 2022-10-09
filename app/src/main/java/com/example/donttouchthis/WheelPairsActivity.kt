package com.example.donttouchthis

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.commons.lang3.math.NumberUtils.isNumber
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Flow
import kotlin.system.measureTimeMillis

class WheelPairsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val time = measureTimeMillis {
            val expListView: ExpandableListView = findViewById(R.id.expListView)

            val groupTo = intArrayOf(android.R.id.text1)
            val childTo = intArrayOf(android.R.id.text1)
            val childDataList: ArrayList<ArrayList<Map<String, String>>> = ArrayList()
            val groupDataList: ArrayList<Map<String, String>> = ArrayList()
            var childDataItemList: ArrayList<Map<String, String>> = ArrayList()
            val groupFrom = arrayOf("groupName")
            val childFrom = arrayOf("Details")

            // Intent
            val wPath: String = intent.getStringExtra("wheelsFilePath") as String
            val bPath: String = intent.getStringExtra("bringsFilePath") as String

            val searchString: String =
                intent.getStringExtra("searchQ")?.trim()?.replace("\\s+".toRegex(), ",") as String
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

            calcTotalRun(wheelData, bearingData, searches, numWheel, numBearing)

            // expandableListAdapterData
            for (i in 0 until numWheel) {
                for (search: String in searches) {
                    if (shouldShow(wheelData[i][0], wheelData[i][4], search)) {
                        var map = HashMap<String, String>()
                        var map1 = HashMap<String, String>()
                        map1["groupName"] = "Номер колесной пары - " + wheelData[i][2]
                        groupDataList.add(map1)
                        map["Details"] =
                            "Номер вагона: " + wheelData[i][0] + " \nПозция: " + wheelData[i][1] + "\nПробег ПР: " + wheelData[i][3] + "\nОбший пробег: " + wheelData[i][4];
                        childDataItemList.add(map)
                        childDataList.add(childDataItemList)
                        childDataItemList = ArrayList()
                    }
                }
            }

            // set adapter
            val adapter = SimpleExpandableListAdapter(
                this, groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, childDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo
            )
            expListView.setAdapter(adapter)

            setInfoText(" Найдено позиций: ${childDataList.toArray().size}")
        }
        println(time)
}

    private fun shouldShow(el0:String, el4:String, search:String): Boolean {
            if (search == el0 || search.isBlank())
                return (el4.split(" ")[0].toInt() > 2000000) || el4.contains("и")
            return false
        }

    private fun calcTotalRun(wheelData: Array<Array<String>>, bearingData: Array<Array<String>>, searches:List<String>, numWheel:Int, numBearing:Int) {
        for (i in 0 until numWheel) {
            for (search: String in searches) {

                if (search == wheelData[i][0] || search.isEmpty()) {

                    for (j in 0 until numBearing) {

                        if (bearingData[j][0].isNotEmpty() && wheelData[i][2].toInt() == bearingData[j][0].toInt()) {
                                wheelData[i][4] =
                                    if (isNumber(bearingData[j][1]) && bearingData[j][1].toInt() != 0) {
                                        (wheelData[i][3].toInt() + bearingData[j][1].toInt()).toString()
                                    } else
                                        wheelData[i][3] + " и " + bearingData[j][1]

                        }
                    }
                }
            }
        }
    }


    private fun setBearingsData(arr:Array<Array<String>>, num:Int, sheet: Sheet?): Array<Array<String>> {

        for (i in 0 until num) {
            var wpN: String = sheet?.getRow(i+1)?.getCell(1).toString().replace(".0","").trim()
            var run: String = sheet?.getRow(i+1)?.getCell(3).toString().replace(".0","").trim()
            arr[i][0] = wpN
            arr[i][1] = run
        }
        return arr
    }

    private fun setWheelsData(arr:Array<Array<String>>, num:Int, sheet: Sheet?): Array<Array<String>> {
        for (i in 0 until num) {
            val tN  = sheet?.getRow(i+1)?.getCell(1).toString().replace(".0", "").trim()
            val pos = sheet?.getRow(i+1)?.getCell(4).toString().replace(".0", "").trim()
            val wpN = sheet?.getRow(i+1)?.getCell(5).toString().replace(".0", "").trim()
            val run = sheet?.getRow(i+1)?.getCell(12).toString().replace(".0","").trim()

            arr[i][0] = tN
            arr[i][1] = pos
            arr[i][2] = wpN
            arr[i][3] = run
            arr[i][4] = arr[i][3]

        }
        return arr
    }

    private fun getSheet(path: String):Sheet? {
        return try {
            val f = File(path)
            val fis = FileInputStream(f)
            val wb: Workbook = WorkbookFactory.create(fis)
            wb.getSheetAt(0)
        }catch (ex:Exception){
            setInfoText("$ex")
            null
        }
    }

    private fun getNumberOfRows(sheet:Sheet?):Int = if (sheet == null) 0 else (sheet.physicalNumberOfRows - 1)

    private fun setInfoText(text:String){
        var eText:TextView = findViewById(R.id.eTextView)
        eText.append("\n$text")
    }
}