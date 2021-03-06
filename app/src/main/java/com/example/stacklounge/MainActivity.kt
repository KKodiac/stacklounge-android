package com.example.stacklounge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.stacklounge.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rbddevs.splashy.Splashy
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Splashy(this) 		 // For JAVA : new Splashy(this)
            .setLogo(R.drawable.stackloungeicon)
            .setTitle("Stack Lounge")
            .setTitleColor(R.color.stcolor)
            .setSubTitle("개발자들의 놀이터")
            .setSubTitleColor(R.color.stcolor)
            .setProgressColor(R.color.white)
            .setBackgroundColor("#FFFFFF")
            .setFullScreen(true)
            .showProgress(true)
            .setProgressColor(R.color.stcolor)
            .setTime(2500)
            .show()
        isFirebaseConnected()
        configureBottomNavigation()
    }

    //firebase 연결됐는지 체크
    private fun isFirebaseConnected(){
        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }

    //Fragment 구성
    private fun configureBottomNavigation() {
        xml_main_viewpaper.adapter = AdapterMainFragment(supportFragmentManager, 4)
        xml_main_tablayout.setupWithViewPager(xml_main_viewpaper)

        val viewBtmNaviMain : View = this.layoutInflater.inflate(R.layout.activity_btm_navigation_main, null, false)
        xml_main_tablayout.getTabAt(0)!!.customView = viewBtmNaviMain.findViewById(R.id.main_btn_favorite)  as RelativeLayout
        xml_main_tablayout.getTabAt(1)!!.customView = viewBtmNaviMain.findViewById(R.id.main_btn_search)  as RelativeLayout
        xml_main_tablayout.getTabAt(2)!!.customView = viewBtmNaviMain.findViewById(R.id.main_btn_community)   as RelativeLayout
        xml_main_tablayout.getTabAt(3)!!.customView = viewBtmNaviMain.findViewById(R.id.main_btn_user)     as RelativeLayout
    }
}