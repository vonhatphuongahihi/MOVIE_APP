import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.squareup.picasso.Picasso

class RecyclerViewMovieAdapter(
    private val context: Context,
    private val movieList: List<Movie>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerViewMovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieItem = movieList[position]
        holder.bind(movieItem)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val imageViewBanner: ImageView = itemView.findViewById(R.id.movies_banner)
        private val textViewMovieName: TextView = itemView.findViewById(R.id.movies_title)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val movie = movieList[position]
                itemClickListener.onItemClick(movie)

            }
        }

        fun bind(movie: Movie) {
            Picasso.get().load(movie.bannerURL).into(imageViewBanner)
            textViewMovieName.text = "${movie.name} (${movie.releaseYear})"
        }
    }

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
    }
}
