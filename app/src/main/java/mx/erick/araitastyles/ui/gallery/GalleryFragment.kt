package mx.erick.araitastyles.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import mx.erick.araitastyles.adapter.ProductAdapter
import mx.erick.araitastyles.databinding.FragmentGalleryBinding
import mx.erick.araitastyles.model.ProductModel

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var productArrayList: ArrayList<ProductModel>
    private lateinit var myAdapter: ProductAdapter

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.productsRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        productArrayList = arrayListOf()

        myAdapter = ProductAdapter(productArrayList, requireContext())

        recyclerView.adapter = myAdapter

        EventListener()


        return root
    }

    private fun EventListener() {
        db = FirebaseFirestore.getInstance()

        db.collection("products").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (document : DocumentChange in value?.documentChanges!!){
                    if (document.type == DocumentChange.Type.ADDED){

                        productArrayList.add(document.document.toObject(ProductModel::class.java))
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