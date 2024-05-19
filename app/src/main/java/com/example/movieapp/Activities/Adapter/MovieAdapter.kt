import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.movieapp.R
import com.squareup.picasso.Picasso


class MovieAdapter(private val context: Context, private val movieList: List<Movie>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return movieList.size
    }

    override fun getItem(position: Int): Any {
        return movieList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView

        itemView = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false)
        var view: View = View.inflate(context, R.layout.movie_item, null)
        var imageViewBanner = view.findViewById<ImageView>(R.id.imageViewBanner)
        var textViewMovieName = view.findViewById<TextView>(R.id.textViewMovieName)
        var textViewReleaseYear = view.findViewById<TextView>(R.id.textViewReleaseYear)
        var movieItem: Movie = movieList.get(position)
        Picasso.get().load(movieItem.bannerURL).into(imageViewBanner)
        textViewMovieName.text = movieItem.name
        textViewReleaseYear.text = movieItem.releaseYear.toString()

        return view


    }

    private class ViewHolder {
        lateinit var imageViewBanner: ImageView
        lateinit var textViewMovieName: TextView
        lateinit var textViewReleaseYear: TextView
    }
}

data class Movie(
    val id: String = "",
    var name: String? = null,        // Tên của bộ phim
    var releaseYear: Int = 0,
    var bannerURL: String? = null,
    var videoUrl: String? = null,
    var director: String? = null,
    var actor: String? = null,
    var age: Int = 0,
    var description: String? = null,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(releaseYear)
        parcel.writeString(bannerURL)
        parcel.writeString(videoUrl)
        parcel.writeString(director)
        parcel.writeString(actor)
        parcel.writeInt(age)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}