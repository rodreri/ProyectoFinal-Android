package mx.erick.araitastyles.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import mx.erick.araitastyles.adapter.PedidoAdapter
import mx.erick.araitastyles.databinding.FragmentHomeBinding
import mx.erick.araitastyles.model.PedidoModel
import mx.erick.araitastyles.model.ProductModel

import java.util.EventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidosArrayList: ArrayList<PedidoModel>
    private lateinit var myAdapter: PedidoAdapter

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.pedidossRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        pedidosArrayList = arrayListOf()

        myAdapter = PedidoAdapter(pedidosArrayList, requireContext())

        recyclerView.adapter = myAdapter


        EventListener()

        return root
    }

    private fun EventListener() {
        db = FirebaseFirestore.getInstance()

        val preferencias =
            context?.getSharedPreferences("mx.erick.araitastyles.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        var email: String? = preferencias?.getString("email", null)

        db.collection("users").document(email!!).collection("pedidos").addSnapshotListener(object :
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (document : DocumentChange in value?.documentChanges!!){
                    if (document.type == DocumentChange.Type.ADDED){

                        pedidosArrayList.add(document.document.toObject(PedidoModel::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}