import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R.layout.popup
import com.example.basicmap.lib.Met
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesViewModel
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.android.synthetic.main.weather.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlacesListAdapter(val context: Context, val placesList: MutableList<Place>?) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {
    private val placesViewModel = PlacesViewModel()
    inner class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(place: Place, pos: Int) {
            itemView.locationNameView.text = place.address
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

            itemView.popup.visibility = View.VISIBLE

            // This is a bit ugly, but works.
            // Would be cool to push the coroutines to livedata and just observe here, we'll see
            // if that's doable in the future.
            GlobalScope.launch {
                val weather = Met().locationForecast(place.position)
                withContext(Dispatchers.Main) {
                    itemView.popup.windSpeedView.text =
                        "Vindhastighet: ${weather.properties.timeseries[0].data.instant.details.wind_speed} m/s"
                    itemView.popup.maxGustView.text =
                        "Max vindkast: ${weather.properties.timeseries[0].data.instant.details.wind_speed_of_gust} m/s"
                    itemView.popup.temperatureView.text =
                        "Temperatur: ${weather.properties.timeseries[0].data.instant.details.air_temperature} °C"
                    itemView.popup.precipitationView.text =
                        "Regn: ${weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount} mm"
                    itemView.popup.fogView.text =
                        "Tåke: ${weather.properties.timeseries[0].data.instant.details.fog_area_fraction}%"
                    itemView.popup.textView.text = "Klikk for neste dagers værvarsel"
                }
            }

        }
        fun slettPlace(pos: Int) {
            placesList?.removeAt(pos)
            placesViewModel.getPlaces().value = placesList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view = LayoutInflater.from(context).inflate(popup, parent, false)
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