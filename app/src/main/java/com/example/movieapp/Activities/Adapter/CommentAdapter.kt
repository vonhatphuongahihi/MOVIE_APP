
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.movieapp.R

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

        var view: View = View.inflate(context, R.layout.comment_card, null)
        var Name = view.findViewById<TextView>(R.id.text_movie)
        var Content = view.findViewById<TextView>(R.id.text_binh_luan)
        var CommentItem: Comment = CommentList.get(position)

        Name.text = CommentItem.name
        Content.text=CommentItem.content

        return view


    }

    }
data class Comment(
    var id: String?="",
    var name: String?=null,
    var content: String? = null,
    var uid: String? = null,
    var movieId: String? = null,
)