package com.kuldeep.bookhub.activity


import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kuldeep.bookhub.R
import com.kuldeep.bookhub.database.BookDatabase
import com.kuldeep.bookhub.database.BookEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_description.*
import org.json.JSONException
import org.json.JSONObject


class DescriptionActivity : AppCompatActivity() {
    lateinit var bookName: TextView
    lateinit var bookAuthor:TextView
    lateinit var bookPrice: TextView
    lateinit var bookImage: ImageView
    lateinit var bookrating: TextView
    lateinit var bookDesc: TextView
    lateinit var addToFavourites: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolBar:Toolbar


    var bookId:String?="100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        bookName=findViewById(R.id.txtBookNameInfo)
        bookAuthor=findViewById(R.id.txtBookAuthorInfo)
        bookPrice=findViewById(R.id.txtBookPriceInfo)
        bookImage=findViewById(R.id.imgBookInfo)
        bookrating=findViewById(R.id.txtBookRatingInfo)
        bookDesc=findViewById(R.id.txtBookDesc)
        addToFavourites=findViewById(R.id.btnFavour)
        progressBar=findViewById(R.id.progressBarDesc)
        progressBar.visibility= View.VISIBLE
        progressLayout=findViewById(R.id.progressLayoutDesc)
        progressLayout.visibility=View.VISIBLE

        toolBar=findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.title="Book Details"

        if (intent!=null){
            bookId=intent.getStringExtra("book_id")

        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some UnExcepted Error Occurred",Toast.LENGTH_SHORT).show()
        }
        if (bookId=="100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
        }
        val queue= Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"

        val jsonParams= JSONObject()
        jsonParams.put("book_id",bookId)

        val jsonRequest=object:JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener {
        try {
            val success=it.getBoolean("success")
            if (success){
                val bookJsonObject=it.getJSONObject("book_data")
                progressLayout.visibility=View.GONE

                val bookImageUrl =bookJsonObject.getString("image")

                Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookInfo)
                bookName.text=bookJsonObject.getString("name")
                bookAuthor.text=bookJsonObject.getString("author")
                bookPrice.text=bookJsonObject.getString("price")
                bookrating.text=bookJsonObject.getString("rating")
                bookDesc.text=bookJsonObject.getString("description")

                val bookEntity=BookEntity(
                    bookId?.toInt() as Int,
                    bookName.text.toString(),
                    bookAuthor.text.toString(),
                    bookPrice.text.toString(),
                    bookrating.text.toString(),
                    bookDesc.text.toString(),
                    bookImageUrl
                )
                val checkFav= DBAsyncTask(applicationContext,bookEntity,1).execute()
                val isFav=checkFav.get()
                if (isFav){
                    btnFavour.text="Remove From favourites"
                    val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                    btnFavour.setBackgroundColor(favColor)
                } else{
                    btnFavour.text="Add to favourites"
                    val nofavColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                    btnFavour.setBackgroundColor(nofavColor)
                }

                btnFavour.setOnClickListener {
                    if (!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                        val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                        val result=async.get()
                        if (result){
                            Toast.makeText(this@DescriptionActivity,"Book Added to Favourites",Toast.LENGTH_SHORT).show()
                            btnFavour.text="Remove From favourites"
                            val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                            btnFavour.setBackgroundColor(favColor)
                        } else{
                            Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                             }
                    }else{
                        val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                        val result=async.get()
                        if (result){
                            Toast.makeText(this@DescriptionActivity,"Book Removed from Favourites",Toast.LENGTH_SHORT).show()
                            btnFavour.text="Add to favourites"
                            val nofavColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                            btnFavour.setBackgroundColor(nofavColor)
                        }else{
                            Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()

                        }

                    }
                }
            }
            else{
                Toast.makeText(this@DescriptionActivity,"Some Error Ocurred",Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException){
            Toast.makeText(this@DescriptionActivity,"Some Json Error Ocurred",Toast.LENGTH_SHORT).show()
        }
        },Response.ErrorListener {
            Toast.makeText(this@DescriptionActivity,"Some Volley Error Ocurred",Toast.LENGTH_SHORT).show()
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "7dfdc86f820a48"
                return headers
            }
        }
        queue.add(jsonRequest)

    }
    class DBAsyncTask(val context:Context,val bookEntity: BookEntity,val mode:Int) : AsyncTask<Void, Void, Boolean>() {
        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
           when(mode){
               1 ->{
                   //Check db if the book is favourite is or not
                   val book:BookEntity?=db.bookDao().getBooksById(bookEntity.book_id.toString())
                   db.close()
                   return book!=null

               }

               2 ->{
                   //Save the book into db as favourite
                   db.bookDao().insertBook(bookEntity)
                   db.close()
                   return true

               }

               3->{
                   //Remove the favourite from the book
                   db.bookDao().deleteBook(bookEntity)
                   db.close()
                   return true


               }


           }
            return false
        }
    }
}