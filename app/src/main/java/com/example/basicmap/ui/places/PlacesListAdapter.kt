import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R.layout.place_kort
import com.example.basicmap.lib.Met
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesViewModel
import kotlinx.android.synthetic.main.place_kort.view.*
import kotlinx.android.synthetic.main.weather.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


class PlacesListAdapter(val context: Context, val placesList: MutableList<Place>?) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {
    private val placesViewModel = PlacesViewModel()
    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(place: Place, pos: Int) {
            itemView.locationNameView.text = place.address
            itemView.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_on)
            itemView.lagreLokasjonsKnapp.setOnClickListener {
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

            itemView.cardView

            // This is a bit ugly, but works.
            // Would be cool to push the coroutines to livedata and just observe here, we'll see
            // if that's doable in the future.
            GlobalScope.launch {
                val weather = Met().locationForecast(place.position)
                withContext(Dispatchers.Main) {
                    val weatherIconName = weather.properties.timeseries[0].data.next_1_hours.summary.symbol_code
                    val id = context.resources.getIdentifier(weatherIconName, "mipmap", context.packageName)
                    itemView.cardView.weatherImageView.setImageResource(id)

                    var tempNow = weather.properties.timeseries[0].data.instant.details.air_temperature?.toDouble()?.roundToInt().toString()

                    itemView.cardView.precipitationView.text = "NEDBØR\n${weather.properties.timeseries[0].data.instant.details.fog_area_fraction}%" //regn eller nedbør riktig her?
                    itemView.cardView.visibilityView.text = "TÅKE\n${weather.properties.timeseries[0].data.instant.details.fog_area_fraction}%"
                    itemView.cardView.kpindexView.text = "KP\n3"

                    itemView.cardView.tempValue.text = "${tempNow}°C"

                }
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
        val place = placesList!!.elementAt(position)
        holder.setData(place, position)
    }


    override fun getItemCount(): Int {
        return placesList!!.size
    }

}