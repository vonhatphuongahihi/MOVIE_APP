import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.movieapp.R


class MovieAdapter(private val context: Context, private val movieList: List<Movie>) : BaseAdapter() {

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
            var view:View= View.inflate(context, R.layout.movie_item,null)
            var imageViewBanner = view.findViewById<ImageView>(R.id.imageViewBanner)
             var textViewMovieName = view.findViewById<TextView>(R.id.textViewMovieName)
           var textViewReleaseYear = view.findViewById<TextView>(R.id.textViewReleaseYear)
           var movieItem:Movie=movieList.get(position)
            imageViewBanner.setImageResource(movieItem.banner)
            textViewMovieName.text=movieItem.name
            textViewReleaseYear.text=movieItem.releaseYear.toString()

      return  view


    }

    private class ViewHolder {
        lateinit var imageViewBanner: ImageView
        lateinit var textViewMovieName: TextView
        lateinit var textViewReleaseYear: TextView
    }
}
data class Movie(
    val banner: Int=0,         // Ảnh đại diện của bộ phim (Resource ID của ảnh)
    val name: String?=null,        // Tên của bộ phim
    val releaseYear: Int=0,     // Năm phát hành của bộ phim

)