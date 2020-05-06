import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R.layout.place_kort
import com.example.basicmap.lib.Met
import com.example.basicmap.lib.populateWeather
import com.example.basicmap.ui.places.LivePlace
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesFragment
import com.example.basicmap.ui.places.PlacesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.place_kort.view.*
import kotlinx.android.synthetic.main.weather.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlacesListAdapter(val fragment: PlacesFragment, val placesList: MutableList<Place>?) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {
    private val placesViewModel = PlacesViewModel()
    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(place: Place, pos: Int) {
            itemView.locationNameView.text = place.address
            itemView.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_on)
            itemView.lagreLokasjonsKnapp.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(fragment.requireActivity())
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

            itemView.gotoButton.setOnClickListener {
                fragment.homeViewModel.getPlace().value = place
                fragment.requireActivity().view_pager.setCurrentItem(0, true)
            }

            itemView.cardView

            val live = LivePlace()

            live.weather.observe(fragment.viewLifecycleOwner, Observer {
                if (it == null)
                    return@Observer
                populateWeather(fragment.requireContext(), itemView.cardView, it)
            })
            live.place.value = place
        }

        fun slettPlace(pos: Int) {
            placesList?.removeAt(pos)
            placesViewModel.getPlaces().value = placesList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view = LayoutInflater.from(fragment.    requireContext()).inflate(place_kort, parent, false)
        return PlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = placesList!!.elementAt(position)
        holder.setData(place, position)
    }


    override fun getItemCount(): Int {
        return placesList!!.size
    }

}