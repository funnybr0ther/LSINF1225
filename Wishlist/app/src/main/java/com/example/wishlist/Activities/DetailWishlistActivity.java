package com.example.wishlist.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wishlist.Adapters.ProductAdapter;
import com.example.wishlist.Class.Product;
import com.example.wishlist.Class.ProductDatabaseHelper;
import com.example.wishlist.Class.WishlistDatabaseHelper;
import com.example.wishlist.Fragment.ChangeWishlistNameFragment;
import com.example.wishlist.R;

import java.util.ArrayList;

public class DetailWishlistActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int userID;
    private int wishlistID;
    WishlistDatabaseHelper dbWishlist;
    ArrayList<Product> products;
    private boolean isMyWishlist;
    private int receiverID;


    /**
     *Display a custom layout for the user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlist_detail);
        dbWishlist = new WishlistDatabaseHelper(getApplicationContext());
        Intent intent= getIntent();

        //check que les elements necessaire à l'activite sont present
        if (intent.hasExtra("userID")){
            userID = intent.getIntExtra("userID",-1);
        } else{
            Intent backToLogin=new Intent(this,LoginActivity.class);
            startActivity(backToLogin);
        }

        if (intent.hasExtra("wishlistID")){
            wishlistID = intent.getIntExtra("wishlistID",-1);
        } else{
            Intent backToWishlist=new Intent(this,LoginActivity.class);
            backToWishlist.putExtra("userID", userID);
            startActivity(backToWishlist);
        }
        if (intent.hasExtra("wishlistName")){
           String wishlistName = intent.getStringExtra("wishlistName");
           TextView title = findViewById(R.id.wishlistToolbarTitle);
           title.setText(wishlistName);
        }
        if (intent.hasExtra("receiverID")){
            receiverID = intent.getIntExtra("receiverID",-1);
            if(userID != receiverID){
                isMyWishlist = false;
            }
        }
        if (intent.hasExtra("isMyWishlist")){
            isMyWishlist = intent.getBooleanExtra("isMyWishlist",false);
        }

        if(!isMyWishlist){
            View addProductBtn = findViewById(R.id.addProductBtn);
            ((ViewGroup) addProductBtn.getParent()).removeView(addProductBtn);
            View changeName = findViewById(R.id.changeNameButton);
            ((ViewGroup) changeName.getParent()).removeView(changeName);
        }

        //spinner
        Spinner spinnerProduct = findViewById(R.id.spinnerProduct);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,R.array. sortProduct, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(spinnerAdapter);
        spinnerProduct.setOnItemSelectedListener(this);
    }

    /**
     * Uptade the layout
     */
    public void layoutUpdate(){
        //Va chercher dans la BDD les product d'une wishlist grace a sa wishlistID
        ListView wishlistListView = findViewById(R.id.wishlist_DetailView);
        products = getProductArray();
        wishlistListView.setAdapter(new ProductAdapter(this, products));
    }

    /**
     * get a ArrayList with all the products in the wishlist
     * @return ArrayList of product
     */
    public ArrayList<Product> getProductArray(){
        ProductDatabaseHelper dbProduct = new ProductDatabaseHelper(getApplicationContext());
        ArrayList<Product> products = new ArrayList<Product>();
        int[] prod = dbWishlist.getProducts(wishlistID);
        for(int productID : prod){
            products.add(dbProduct.getProductFromID(productID));
        }
        return products;
    }

    /**
     * Start an activity for create a new product
      * @param view add product button
     */
    public void pressAddButton(View view){
        Intent intent = new Intent(this,EditProductActivity.class);
        intent.putExtra("productID",-1);
        startActivityForResult(intent,4);
    }

    /**
     * Start a fragment in which you can manage your wishlist
     * @param view edit wishlist button
     */
    public void pressChangeButton(View view){
        ChangeWishlistNameFragment dialog=new ChangeWishlistNameFragment();
        Bundle args = new Bundle();
        args.putInt("wishlistID", wishlistID);
        args.putInt("userID", userID);
        dialog.setArguments(args);
        dialog.show(this.getSupportFragmentManager(),"Change name");
    }

    /**
     * Update the layout title
     * @param newName new name of the wishlist
     */
    public void fragmentReturn(String newName){
        TextView title = findViewById(R.id.wishlistToolbarTitle);
        title.setText(newName);
    }

    /**
     * Start an activity with the detail of a product
     * @param productPosition position of the product in the wishlist
     */
    public void productDetail(int productPosition){
        Intent intent1=new Intent(this, ViewProductActivity.class);
        intent1.putExtra("productID", dbWishlist.getProducts(wishlistID)[productPosition]);
        intent1.putExtra("receiverID", receiverID);
        intent1.putExtra("userID", userID);
        intent1.putExtra("isMyProduct", isMyWishlist);
        startActivityForResult(intent1,1);
    }

    /**
     * Manage the startActivityForResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 4){
            if (resultCode == Activity.RESULT_OK) {
                if(data.hasExtra("newProduct")) {
                    int productID = data.getIntExtra("newProduct", -1);
                    dbWishlist.addProduct(productID, wishlistID);
                    addProductReturn();
                }
            }
        }
        else if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Log.d("BILIBU", "onActivityResult: update to products view");
                addProductReturn();
            }
        }
    }

    /**
     * Update the layout when a product is add
     */
    public void addProductReturn(){
        ListView wishlistListView = findViewById(R.id.wishlist_DetailView);
        products = getProductArray();
        wishlistListView.setAdapter(new ProductAdapter(this, products));
    }

    /**
     * keep the layout update
     */
    @Override
    protected void onResume() {
        super.onResume();
        layoutUpdate();
    }

    /**
     * Back to the last activity
     * @param view
     */
    public void onBackPressed(View view) {
        onBackPressed();
    }

    /**
     * Sort the product from the cheapest to the more expensive
     * @param p ArrayList of Product
     * @return ArrayList of Product sort
     */
    public ArrayList<Product> sortProductByPriceUp(ArrayList<Product> p){
        int size = p.size();
        for(int i = 0; i < size; i++){
            int minI = i;
            for (int j = i+1; j < size; j++){
                if(p.get(j).getPrice() < p.get(minI).getPrice()){
                    minI = j;
                }
            }
            Product temp = p.get(minI);
            p.set(minI,p.get(i));
            p.set(i,temp);
        }
        return p;
    }

    /**
     * Sort the product from the more expensive to the cheapest
     * @param p ArrayList of Product
     * @return ArrayList of Product sort
     */
    public ArrayList<Product> sortProductByPriceDown(ArrayList<Product> p){
        int size = p.size();
        for(int i = 0; i < size; i++){
            int maxI = i;
            for (int j = i+1; j < size; j++){
                if(p.get(j).getPrice() > p.get(maxI).getPrice()){
                    maxI = j;
                }
            }
            Product temp = p.get(maxI);
            p.set(maxI,p.get(i));
            p.set(i,temp);
        }
        return p;
    }

    /**
     * Spinner
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if(position == 0){
            layoutUpdate();
        }else if(position == 1){
            ListView wishlistListView = findViewById(R.id.wishlist_DetailView);
            ArrayList<Product> p = sortProductByPriceUp(products);
            wishlistListView.setAdapter(new ProductAdapter(this, p));
        }else if(position == 2) {
            ListView wishlistListView = findViewById(R.id.wishlist_DetailView);
            ArrayList<Product> p = sortProductByPriceDown(products);
            wishlistListView.setAdapter(new ProductAdapter(this, p));
        }
    }

    /**
     * spinner do nothing
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
