import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R.layout.activity_main
import com.example.basicmap.R.layout.place_kort
import com.example.basicmap.lib.setupWeatherElement
import com.example.basicmap.ui.places.LivePlace
import com.example.basicmap.ui.places.PlacesFragment
import com.example.basicmap.ui.places.PlacesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.place_kort.view.*
import kotlinx.android.synthetic.main.weather.view.*


class PlacesListAdapter(val fragment: PlacesFragment, val placesList: MutableList<LivePlace>?) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {
    private val placesViewModel = PlacesViewModel()
    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        fun setData(livePlace: LivePlace, pos: Int) {
            itemView.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_on)


            itemView.locationNameView.visibility = GONE //removes the textview of the original popup

            itemView.locationNameEditTextView.visibility = VISIBLE //makes an edit text view visible
            itemView.storeNewAddressButton.visibility = VISIBLE

            itemView.lagreLokasjonsKnapp.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(fragment.requireActivity())
                builder.setTitle("Slett lokasjon/plass")
                builder.setMessage("Er du sikker på at du vil slette \n" + livePlace.address + " ?")
                builder.setPositiveButton("Ja") { dialog, which ->
                    slettPlace(pos)
                    livePlace.place.value?.favorite = false
                }
                builder.setNegativeButton("Nei") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }


            itemView.storeNewAddressButton.setOnClickListener{

                var newAddress: String = itemView.locationNameEditTextView.getText().toString()
                livePlace.place.value?.address = newAddress

                val message: String = "Du har endret addressenavn på denne favorittlokasjonen."

                //MAKE A TOAST
                //MAKE A TOAST
                //MAKE A TOAST

            }

            itemView.gotoButton.setOnClickListener {
                fragment.homeViewModel.getPlace().place.value = livePlace.place.value
                fragment.requireActivity().view_pager.setCurrentItem(0, true)
            }

            itemView.cardView

            setupWeatherElement(
                fragment.requireContext(),
                fragment.viewLifecycleOwner,
                livePlace,
                itemView
            )
        }

        fun slettPlace(pos: Int) {
            placesList?.removeAt(pos)
            placesViewModel.getPlaces().value = placesList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(place_kort, parent, false)
        return PlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val livePlace = placesList!!.elementAt(position)
        holder.setData(livePlace, position)
    }


    override fun getItemCount(): Int {
        return placesList!!.size
    }

}