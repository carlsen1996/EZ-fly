import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.place_kort.view.*
import com.example.basicmap.R.layout.place_kort
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesViewModel


class PlacesListAdapter(val context: Context, val placesList: MutableList<Place>?) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {
    private val placesViewModel = PlacesViewModel()
    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(place: Place, pos: Int) {
            itemView.placeAdresse.text = place.address
            itemView.placeDeleteKnapp.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                builder.setTitle("Slett lokasjon/plass")
                builder.setMessage("Er du sikker på at du vil slette \n" + place.address + " ?")
                builder.setPositiveButton("Ja") { dialog, which ->
                    slettPlace(pos)
                    place.favorite = false
                }
                builder.setNegativeButton("Nei") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
        fun slettPlace(pos: Int) {
            placesList?.removeAt(pos)
            placesViewModel.getPlaces().value = placesList
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view = LayoutInflater.from(context).inflate(place_kort, parent, false)
        return PlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place: Place = placesList!!.elementAt(position)
        holder.setData(place, position)
    }


    override fun getItemCount(): Int {
        return placesList!!.size
    }

}