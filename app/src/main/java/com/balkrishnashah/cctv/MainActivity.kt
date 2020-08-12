package com.balkrishnashah.cctv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import com.pedro.vlc.VlcListener
import com.pedro.vlc.VlcVideoLibrary
import com.rvirin.onvif.onvifcamera.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnvifListener, VlcListener {

    var vlcVideoLibrary: VlcVideoLibrary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        currentDevice = OnvifDevice("192.168.43.1:8080", "admin", "12345678")
        currentDevice.listener =this
        currentDevice.getDeviceInformation()

    }

    override fun requestPerformed(response: OnvifResponse) {
        Log.d("ONVIF", response.parsingUIMessage)

       if (response.request.type == OnvifRequest.Type.GetDeviceInformation) {
            currentDevice.getProfiles()
        } else if (response.request.type == OnvifRequest.Type.GetProfiles) {
           currentDevice.mediaProfiles.firstOrNull()?.let {
               currentDevice.getStreamURI(it)
           }
        } else if (response.request.type == OnvifRequest.Type.GetStreamURI) {
           currentDevice.rtspURI?.let { uri ->
               Log.d("ONVIF", "Stream URI retrieved: ${currentDevice.rtspURI}")
               val SurfaceView = findViewById<SurfaceView>(R.id.surfaceView)
               vlcVideoLibrary = VlcVideoLibrary(this, this, surfaceView)
               vlcVideoLibrary?.play(uri)
           }

        }
    }

    override fun onComplete() {
        Toast.makeText(this, "video completed", Toast.LENGTH_SHORT).show()
    }

    override fun onError() {
        Toast.makeText(this, "video end", Toast.LENGTH_SHORT).show()
    }
}