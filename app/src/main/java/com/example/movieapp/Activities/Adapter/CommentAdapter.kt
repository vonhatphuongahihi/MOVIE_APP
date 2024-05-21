
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.movieapp.R
import com.squareup.picasso.Picasso

class CommentAdapter(private val context: Context, private val CommentList: List<Comment>) :
    BaseAdapter(){
    override fun getCount(): Int {
        return CommentList.size
    }
    override fun getItem(position: Int): Any {
        return CommentList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var itemView = convertView

        itemView = LayoutInflater.from(context).inflate(R.layout.comment_card, parent, false)
        var view: View = View.inflate(context, R.layout.comment_card, null)
        var Name = view.findViewById<TextView>(R.id.text_movie)
        var Content = view.findViewById<TextView>(R.id.text_binh_luan)
        var imageViewBanner = view.findViewById<ImageView>(R.id.avatar)

        var CommentItem: Comment = CommentList.get(position)

        if (CommentItem.avatarURL != "") {
            Picasso.get().load(CommentItem.avatarURL).into(imageViewBanner)
        }

        Name.text = CommentItem.name
        Content.text=CommentItem.content

        return view


    }

    }
data class Comment(
    var id: String?="",
    var name: String?=null,
    var avatarURL: String?=null,
    var content: String? = null,
    var uid: String? = null,
    var movieId: String? = null,
)