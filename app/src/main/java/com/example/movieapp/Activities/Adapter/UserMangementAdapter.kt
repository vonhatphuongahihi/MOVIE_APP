import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.model.User
import com.google.firebase.database.FirebaseDatabase


class UserMangementAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_manage_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.phoneTextView.text = user.phoneNumber

        // Thiết lập Spinner để hiển thị role hiện tại của người dùng
        val roleArray = holder.itemView.context.resources.getStringArray(R.array.roles_array)
        val rolePosition = roleArray.indexOf(user.role)
        holder.roleSpinner.setSelection(rolePosition)

        // Lắng nghe sự kiện khi người dùng thay đổi role
        holder.roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newRole = roleArray[position]
                if (newRole != user.role) {
                    updateRoleInDatabase(user.phoneNumber!!, newRole)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    private fun updateRoleInDatabase(phomeNumber: String, newRole: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(phomeNumber).child("role").setValue(newRole)
    }
}

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
    val roleSpinner: Spinner = itemView.findViewById(R.id.roleSpinner)
}