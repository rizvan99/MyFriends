package com.example.friends_app.GUI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.friends_app.Model.BEFriend
import com.example.friends_app.R
import com.example.friends_app.data.FriendRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class DetailActivity : AppCompatActivity() {

    private val RESULT_CODE_CREATE = 1
    private val RESULT_CODE_DELETE = 3
    private val RESULT_CODE_UPDATE = 4
    private val PERMISSION_REQUEST_CODE = 5
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE = 101
    var mFile: File? = null
    private var fRep: FriendRepository? = null

    private var checked by Delegates.notNull<Boolean>()
    private var isCreate: Boolean = false
    private lateinit var chosenFriend: BEFriend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val extras: Bundle = intent.extras!!
        isCreate = extras.getBoolean("isCreate")
        if (!isCreate) {
            val friend = extras.getSerializable("friend") as BEFriend
            chosenFriend = friend
            if (chosenFriend.isFavorite) {
                toggleFavorite.isChecked = true
            }
            readPerson()
        } else {
            layoutActivities.visibility = View.INVISIBLE
        }

        toggleFavorite.setOnCheckedChangeListener { _, isChecked ->
            checked = isChecked
            imgFavorite.setImageResource(if (isChecked) R.drawable.ok else R.drawable.notok)
        }

        checkPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isCreate) {
            menuInflater.inflate(R.menu.detail_menu, menu);
            return true;
        }
        return false;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> {
                val intent = Intent()
                intent.putExtra("friendToDelete", chosenFriend)
                setResult(RESULT_CODE_DELETE, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun submitPerson(view: View) {
        val intent = Intent()
        val name = editTextName.text.toString()
        val number = editTextNumber.text.toString()
        val address = editTextAddress.text.toString()
        val isFavorite = checked
        val birthday = editTextBirthday.text.toString()
        val website = editTextWebsite.text.toString()
        val email = editTextEmail.text.toString()
        val location = LatLng(chosenFriend.latitude, chosenFriend.longitude)
        val img = chosenFriend.imagePath ?: ""
        if (isCreate) {
            val newFriend = BEFriend(0, name, number, address, isFavorite, email, website, birthday, location.latitude, location.longitude, img)
            intent.putExtra("newFriend", newFriend)
            setResult(RESULT_CODE_CREATE, intent)
            finish()
        }
        if (!isCreate) {
            val updatedFriend = BEFriend(chosenFriend.id,name, number, address, isFavorite, email, website, birthday, location.latitude, location.longitude, img)
            intent.putExtra("updatedFriend", updatedFriend)
            setResult(RESULT_CODE_UPDATE, intent)
            finish()
        }
    }

    private fun readPerson() {
        editTextName.setText(chosenFriend.name)
        editTextAddress.setText(chosenFriend.address)
        editTextNumber.setText(chosenFriend.phone)
        editTextBirthday.setText(chosenFriend.birthday)
        editTextEmail.setText(chosenFriend.email)
        editTextWebsite.setText(chosenFriend.website)
        imgFavorite.setImageResource(if (chosenFriend.isFavorite) R.drawable.ok else R.drawable.notok)
        txtGetLocation.text = "Location: " + chosenFriend.latitude + chosenFriend.longitude
        imgView.setImageURI(Uri.parse(chosenFriend.imagePath))
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    fun openMap(view: View) {
        val intentMap = Intent(this, MapsActivity::class.java)
        intentMap.putExtra("newMarker", true)
        startActivityForResult(intentMap, 15)
    }

    fun onClickCall(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${chosenFriend.phone}")
        startActivity(intent)
    }

    fun onClickSMS(view: View) {
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("sms:${chosenFriend.phone}")
        sendIntent.putExtra("sms_body", "Hi, it goes well on the android course...")
        startActivity(sendIntent)
    }

    fun onClickEmail(view: View) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        val receivers = chosenFriend.email
        emailIntent.putExtra(Intent.EXTRA_EMAIL, receivers)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test")
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Hej, Hope that it is ok, Best Regards android...;-)")
        startActivity(emailIntent)
    }

    fun onClickWebsite(view: View) {
        val url = "http://wwww." + chosenFriend.website
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun showLocation(view: View) {
        val intentMap = Intent(this, MapsActivity::class.java)
        intentMap.putExtra("showLocation", true)
        intentMap.putExtra("selectedFriend", chosenFriend)
        startActivityForResult(intentMap, 22)
    }

    //Checks if the app has the required permissions, and prompts the user with the ones missing.
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissions = mutableListOf<String>()
        if ( ! isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) ) permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if ( ! isGranted(Manifest.permission.CAMERA) ) permissions.add(Manifest.permission.CAMERA)
        if (permissions.size > 0)
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }
    private fun isGranted(permission: String): Boolean =
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    fun onTakeByFile(view: View) {
        mFile = getOutputMediaFile("Camera01") // create a file to save the image

        if (mFile == null) {
            Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show()
            return
        }

        // create Intent to take a picture

        // create Intent to take a picture
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val applicationId = "com.example.friends_app"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                this,
                "${applicationId}.provider",  //use your app signature + ".provider"
                mFile!!))

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE)
        } else Log.d("TAG", "camera app could NOT be started")
    }

    private fun getOutputMediaFile(folder: String): File? {
        // in an emulated device you can see the external files in /sdcard/Android/data/<your app>.
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("TAG", "failed to create directory")
                return null
            }
        }
        // Create a media file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val postfix = "jpg"
        val prefix = "IMG"
        return File(mediaStorageDir.path +
                File.separator + prefix +
                "_" + timeStamp + "." + postfix)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mImage = findViewById<ImageView>(R.id.imgView)
        when (requestCode) {
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE ->
                if (resultCode == RESULT_OK)
                    showImageFromFile(mImage, null, mFile!!)
                else handleOther(resultCode)
        }

        if (resultCode === 50) {
            val location = data?.extras?.get("newLocation") as LatLng
            txtGetLocation.text = location.toString()
            chosenFriend.latitude = location.latitude
            chosenFriend.longitude = location.longitude
        }
    }

    private fun handleOther(resultCode: Int) {
        if (resultCode == RESULT_CANCELED)
            Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show()
        else Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show()
    }

    private fun showImageFromFile(img: ImageView, txt: TextView?, f: File) {
        chosenFriend.imagePath = Uri.fromFile(f).toString()
        img.setImageURI(Uri.fromFile(f))
        img.scaleType = ImageView.ScaleType.CENTER_CROP
    }

}