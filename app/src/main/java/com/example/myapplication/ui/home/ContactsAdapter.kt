
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.home.Contact

class ContactsAdapter(private val contacts: List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDetailsDialog(contacts[position], itemView)
                }
            }
        }

        private fun showDetailsDialog(contact: Contact, view: View) {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("Contact Details")
            builder.setMessage("Name: ${contact.name}\nPhone: ${contact.phoneNumber}\n연락처 저장 날짜: ${contact.savedDate}\n최근 연락 날짜: ${contact.lastContactedDate}")
            builder.setPositiveButton("OK", null)
            builder.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber
    }

    override fun getItemCount() = contacts.size
}