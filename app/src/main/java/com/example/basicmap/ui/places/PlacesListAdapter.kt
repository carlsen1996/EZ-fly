import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.place_kort.view.*
import kotlinx.android.synthetic.main.fragment_places.view.*
import com.example.basicmap.R
import com.example.basicmap.R.layout.place_kort
import com.example.basicmap.ui.places.PlaceForDrone
import com.google.android.libraries.places.api.model.Place


class PlacesListAdapter(val context: Context, val placesList: MutableList<PlaceForDrone>) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {

    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(place: PlaceForDrone, pos: Int) {
            itemView.placeAdresse.setText(place.adresse)
            itemView.placeDeleteKnapp.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                builder.setTitle("Slett lokasjon/plass")
                builder.setMessage("Er du sikker på at du vil slette \n" + place.adresse + " ?")
                builder.setPositiveButton("Ja") { dialog, which ->
                    slettPlace(pos)
                }
                builder.setNegativeButton("Nei") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
        fun slettPlace(pos: Int) {
            placesList.removeAt(pos)
            notifyDataSetChanged()
            saveData()
        }
        private fun saveData() {
            val sharedPref: SharedPreferences = context.getSharedPreferences("sharedPrefPlaces", AppCompatActivity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            val gson = GsonBuilder().create()
            val json = gson.toJson(placesList)
            editor.putString("placesList", json)
            editor.apply()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view = LayoutInflater.from(context).inflate(place_kort, parent, false)
        return PlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place: PlaceForDrone = placesList.elementAt(position)
        holder.setData(place, position)
    }


    override fun getItemCount(): Int {
        return placesList.size
    }

}