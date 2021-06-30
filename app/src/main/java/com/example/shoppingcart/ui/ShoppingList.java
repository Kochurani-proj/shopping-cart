package com.example.shoppingcart.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.shoppingcart.CreateAndFillValuesInListShoppingRows;
import com.example.shoppingcart.MainActivity;
import com.example.shoppingcart.R;
import com.example.shoppingcart.model.common.ShopItem;
import com.example.shoppingcart.model.firebase.AddShoppingModel;
import com.example.shoppingcart.model.ui.ExpandShopGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ShoppingList extends AppCompatActivity {
ImageButton addShoppingButton;
private CreateAndFillValuesInListShoppingRows createAndFillValuesInListShoppingRows;
private ExpandableListView expandableListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        addShoppingButton=findViewById(R.id.addButton);
        expandableListView=findViewById(R.id.ExpList);
        fetchAndPopulateEntriesInExpandableListView();
        addShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShoppingList.this, CreateShoppingList.class);
                startActivity(intent);

            }
        });

    }

    private void fetchAndPopulateEntriesInExpandableListView() {
        FirebaseDatabase.getInstance().getReference().child("shopping").child(MainActivity.getSubscribeId(ShoppingList.this))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<ExpandShopGroup> groupList=new ArrayList<>();
                for(DataSnapshot areaSnapsnot:dataSnapshot.getChildren()) {
                    AddShoppingModel value = areaSnapsnot.getValue(AddShoppingModel.class);
                    String orderId = areaSnapsnot.getKey();
                    ExpandShopGroup gru1 = new ExpandShopGroup();
                    gru1.setShoppingId(orderId);
                    gru1.setDate(value.getDate());
                    ArrayList<ShopItem> childList = new ArrayList<>();
                    for (ShopItem shopItem : value.getSales().values()) {
                        ShopItem rowChild = new ShopItem();
                        rowChild.setCategory(shopItem.getCategory());
                        rowChild.setProduct(shopItem.getProduct());
                        rowChild.setQuantity(shopItem.getQuantity());
                        rowChild.setUnit(shopItem.getUnit());
                        childList.add(rowChild);
                    }
                    gru1.setItems(childList);
                    groupList.add(gru1);
                }
                Collections.sort(groupList);
                for(int k=1;k<=groupList.size();k++)
                {
                    groupList.get(k-1).setName("Shopping List-" +k);

                }
                createAndFillValuesInListShoppingRows=new CreateAndFillValuesInListShoppingRows(ShoppingList.this,groupList);
                expandableListView.setAdapter(createAndFillValuesInListShoppingRows);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DisplayMetrics metrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width=metrics.widthPixels;
        if(Build.VERSION.SDK_INT <Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            expandableListView.setIndicatorBounds(width-GetPixelFromDips(80),width- GetPixelFromDips(55));
        }
        else
        {
            expandableListView.setIndicatorBoundsRelative(width-GetPixelFromDips(85),width- GetPixelFromDips(55));
        }




    }

    private int GetPixelFromDips(float pixels) {
        final float scale=getResources().getDisplayMetrics().density;
        return(int)(pixels* scale + 0.5f);
    }
}