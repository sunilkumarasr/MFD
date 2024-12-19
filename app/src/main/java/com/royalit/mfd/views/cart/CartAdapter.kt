package com.royalit.mfd.views.cart

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.royalit.mfd.R
import com.royalit.mfd.models.CartModel
import com.royalit.mfd.services.RetrofitClient
import com.bumptech.glide.Glide

class CartAdapter(lwn:LifecycleOwner) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

     var listCart=ArrayList<CartModel>()
    init {
        CardData.cartListMap?.observe(lwn, Observer {
            listCart.clear()
            listCart.addAll(it)
            notifyDataSetChanged()
            Log.d("Observer data","Observer data ${it.size}")
        })
    }
    class CartViewHolder(item: View): RecyclerView.ViewHolder(item) {
                    val txt_prodcut_name=item.findViewById<TextView>(R.id.txt_prodcut_name)
                    val txt_net_price=item.findViewById<TextView>(R.id.txt_net_price)
                    val txt_item_count=item.findViewById<TextView>(R.id.txt_item_count)
                    val lnr_minus_item=item.findViewById<View>(R.id.lnr_minus_item)
                    val lnr_add_item=item.findViewById<View>(R.id.lnr_add_item)
                    val img_product=item.findViewById<ImageView>(R.id.img_product)
                    val imgDelete=item.findViewById<ImageView>(R.id.imgDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_item,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  listCart.size!!
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            var cartModel=listCart.get(position)

        val text = "Service: ${cartModel?.productName}"
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Apply bold style
            0,                        // Start index of "Service: "
            8,                        // End index of "Service: "
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        holder.txt_prodcut_name.text = spannableString

        holder.txt_item_count.text=cartModel?.quantity.toString()
        holder.txt_net_price.text="Price: ₹${cartModel?.price.toString()}"
        holder.lnr_add_item.setOnClickListener {
            if (cartModel != null) {
                CardData.addCart(cartModel)
            }
        }

        Glide.with(holder.img_product.context)
            .load("${RetrofitClient.PRODUCT_IMAGE_PATH}/${cartModel.image}")
            .placeholder(holder.img_product.context.getDrawable(R.drawable.loading_ic))
            .centerCrop()
            .into(holder.img_product);

        holder.lnr_minus_item.setOnClickListener {
            if (cartModel != null) {
                CardData.remove(cartModel)
            }
        }

        holder.imgDelete.setOnClickListener {
            CardData.delete(cartModel)
        }


    }
}