package mx.erick.araitastyles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import mx.erick.araitastyles.R
import mx.erick.araitastyles.model.PedidoModel
import mx.erick.araitastyles.model.ProductModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PedidoAdapter(private val pedidosList: ArrayList<PedidoModel>, private val context: Context): RecyclerView.Adapter<PedidoAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_pedido, parent, false)

        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return pedidosList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val pedido: PedidoModel = pedidosList[position]


        holder.price.text = "Precio: " + pedido.price
        holder.productID.text = "Id del producto: " + pedido.productid
        holder.statusPedido.text = "Status: " + pedido.status
        holder.timePedido.text = "Pedido el " + pedido.time

    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val productID: TextView = itemView.findViewById(R.id.productID)
        val price: TextView = itemView.findViewById(R.id.pricePedido)
        val statusPedido: TextView = itemView.findViewById(R.id.statusPedido)
        val timePedido: TextView = itemView.findViewById(R.id.timePedido)

    }

}