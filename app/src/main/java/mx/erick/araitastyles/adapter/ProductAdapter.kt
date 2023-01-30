package mx.erick.araitastyles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import mx.erick.araitastyles.R
import mx.erick.araitastyles.model.ProductModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductAdapter(private val productList: ArrayList<ProductModel>, private val context: Context): RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int) {

        val db = FirebaseFirestore.getInstance()
        val preferencias =
            context.getSharedPreferences("mx.erick.araitastyles.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        var email: String? = preferencias?.getString("email", null)

        val product: ProductModel = productList[position]

        holder.type.text = product.type
        holder.price.text = product.price
        holder.size.text = product.size
        holder.btn.setOnClickListener{

            db.collection("users").document(email!!).collection("pedidos").document().set(
                hashMapOf("time" to SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date()),
                    "productid" to product.id.toString(),
                    "status" to "pedido",
                    "price" to product.price
                )
            )
        }

        when(product.id) {

            "product01" -> holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mantel1))
            "product02" -> holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mantel2))
            "product03" -> holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.servilleta))
            "product04" -> holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.carpeta))
            else -> print("No hay")

        }
    }


    override fun getItemCount(): Int {
        return productList.size
    }

    public class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val type: TextView = itemView.findViewById(R.id.typeProduct)
        val price: TextView = itemView.findViewById(R.id.priceProduct)
        val size: TextView = itemView.findViewById(R.id.sizeProduct)
        val btn: Button = itemView.findViewById(R.id.btnAdd)
        val image: ImageView = itemView.findViewById(R.id.imgProduct)

    }

}