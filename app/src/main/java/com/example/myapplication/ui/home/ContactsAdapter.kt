import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.home.Contact

class ContactsAdapter(
    private var contacts: List<Contact>,
    private val onContactClicked: (Contact) -> Unit,  // 연락처 클릭 이벤트 핸들러 추가
    private val onContactsUpdated: (List<Contact>) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>(), Filterable {

    private var filteredContacts: List<Contact> = contacts

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 연락처 클릭 시 onContactClicked 호출
                    onContactClicked(filteredContacts[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteDialog(filteredContacts[position])
                }
                true
            }
        }

        private fun showDeleteDialog(contact: Contact) {
            AlertDialog.Builder(itemView.context)
                .setTitle("인연 삭제")
                .setMessage("이 인연을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    deleteContact(contact)
                }
                .setNegativeButton("취소", null)
                .show()
        }

        private fun deleteContact(contact: Contact) {
            val updatedContacts = contacts.toMutableList()
            updatedContacts.remove(contact)
            updateContacts(updatedContacts)
            onContactsUpdated(updatedContacts)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = filteredContacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber
    }

    override fun getItemCount() = filteredContacts.size

    fun sortByLastContactedDate() {
        filteredContacts = filteredContacts.sortedWith(compareBy<Contact> { it.lastContactedDate }.thenBy { it.savedDate })
        notifyDataSetChanged()
    }

    fun sortByName() {
        filteredContacts = filteredContacts.sortedWith(compareBy<Contact> { it.name })
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                filteredContacts = if (charString.isEmpty()) {
                    contacts
                } else {
                    contacts.filter {
                        it.name.contains(charString, true) || it.phoneNumber.contains(charString, true)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredContacts
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredContacts = results?.values as List<Contact>
                notifyDataSetChanged()
            }
        }
    }

    fun getContacts(): List<Contact> {
        return contacts
    }

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        filteredContacts = newContacts
        notifyDataSetChanged()
    }
}
