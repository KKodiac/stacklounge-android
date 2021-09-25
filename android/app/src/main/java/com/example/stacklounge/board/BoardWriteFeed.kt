package com.example.stacklounge.board

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.stacklounge.R
import com.example.stacklounge.fragment.FragmentMainCommunity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_board_write_text.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class BoardWriteFeed : AppCompatActivity() {

    var boardList = arrayListOf<BoardData>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write_text)

        //앱바 숨김
        val actionBarBoardWriteText: ActionBar? = supportActionBar
        actionBarBoardWriteText?.hide()

        // 뒤로가기
        btnBackText.setOnClickListener{
            finish()
        }

        // 글 작성
        btnWriteComplete.setOnClickListener {
            writingText()
        }

        // 글 수정 (BoardShowFeed -> BoardWriteFeed)
        updateBoardText()
        // 글 db 수정 시간+id, feedtime
        val uflag = intent.getStringExtra("flag").toString()
        Log.d("uflag",uflag)
        if(uflag=="1"){
            val ufeedTimeg = intent.getStringExtra("ufeedTime").toString()
            val uuserId = intent.getStringExtra("uuserId").toString()
            val uwritingTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            btnWriteComplete.setOnClickListener {
                // updatefeedTime
                val updatedatabase2 = FirebaseDatabase.getInstance("https://stacklounge-62ffd-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                updatedatabase2.child("board")
                    .child("$ufeedTimeg+$uuserId")
                    .child("updatefeedTime")
                    .setValue(uwritingTime)

                // updatecontents
                var updatecontents = writingContent.text.toString()
                val updatedatabase1 = FirebaseDatabase.getInstance("https://stacklounge-62ffd-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                updatedatabase1.child("board")
                    .child("$ufeedTimeg+$uuserId")
                    .child("contents")
                    .setValue(updatecontents)

                // updatetitle
                var update1 = selectet1.text.toString()
                var update2 = selectet2.text.toString()
                val updatedatabase3 = FirebaseDatabase.getInstance("https://stacklounge-62ffd-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                updatedatabase3.child("board")
                    .child("$ufeedTimeg+$uuserId")
                    .child("title")
                    .setValue("$update1 vs $update2")

                // BoardShowFeed 새로고침 해야함 -> startActivityForResult
                intent.putExtra("selectet11",selectet1.text.toString())
                intent.putExtra("selectet22",selectet2.text.toString())
                intent.putExtra("writingContent11",writingContent.text.toString())
                setResult(Activity.RESULT_OK, intent)
                
                finish()
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun writingText(){
        //realtime db에 BoardData

        val writingTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) //작성시간

        // firebase auth
        val user = Firebase.auth.currentUser

        // db
        val database = FirebaseDatabase.getInstance("https://stacklounge-62ffd-default-rtdb.asia-southeast1.firebasedatabase.app/") // 프로젝트 주소
        val userIdRef = database.reference // userId 불러오는 경로


        userIdRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val aUserId = snapshot.child("current-user/${user?.uid}").child("login").value
                val aUserphoto = snapshot.child("current-user/${user?.uid}").child("avatar_url").value.toString()

//                var boardNumber = snapshot.child("board").child("boardNumber").child("boardNumber").value.toString().toInt()
//                boardNumber++

                var duserId = writingTime +"+"+aUserId.toString()

                val addRef = database.getReference("board/$duserId") // 저장경로


                val aTitle = selectet1.text.toString()+" vs "+selectet2.text.toString() // 제목
                val aContents = writingContent.text.toString()

                // uid는 auth부분에 있다.

                val aUserInfo = hashMapOf(
                    "userId" to aUserId,
                    "contents" to aContents,
                    "title" to aTitle,
                    "feedTime" to writingTime,
                    "userphoto" to aUserphoto
                )

//                val aboardInfo = hashMapOf(
//                    "boardNumber" to boardNumber.toString()
//                )

                if(selectet1.text.toString()=="" || selectet2.text.toString()==""){
                    Toast.makeText(applicationContext,"기술 스택을 입력해주세요.",Toast.LENGTH_SHORT).show()
                }
                else if(aContents==""){
                    Toast.makeText(applicationContext,"내용을 입력해주세요.",Toast.LENGTH_SHORT).show()
                }
                else{
                    boardList.add((BoardData(aTitle,aContents,writingTime,aUserId as String)))
//                    userIdRef.child("board").child("boardNumber").setValue(aboardInfo)
                    addRef.setValue(aUserInfo)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //실패할 때
                Toast.makeText(applicationContext,"DB 에러",Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateBoardText(){
        val uflag = intent.getStringExtra("flag").toString()
        Log.d("uflag",uflag)
        if(uflag=="1"){
            val utitleg = intent.getStringExtra("utitle").toString().split("vs")
            val uboardTitle1 = utitleg[0].trim()
            val uboardTitle2 = utitleg[1].trim()

            val ucontentsg = intent.getStringExtra("ucontents").toString()

            selectet1.setText(uboardTitle1)
            selectet2.setText(uboardTitle2)
            writingContent.setText(ucontentsg)

        }
    }


}