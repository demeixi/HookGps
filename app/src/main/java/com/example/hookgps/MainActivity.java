package com.example.hookgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.example.hookgps.databinding.ActivityMainBinding;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.example.hookgps.server.CustomService;

/*
简介
 1.虚拟定位模块(Xposed模块)，系统级别，配合百度地图使用

使用修改说明:
 1.需要配置AndroidManifest.xml文件中的百度地图key
 2.使用时开启模块，勾选推荐应用即可，无需勾选指定应用，然后重启即可
 3.完全避免了勾选指定应用被检测到Xposed注入

免责声明:
 请勿违法违规使用或修改，本源码只用于学术探讨和研究，所有违法违规的使用和修改与本人无关,在此声明。

下载或使用即同意上面声明
 */

public class MainActivity extends AppCompatActivity  {
    private double la = 22.439353513701644;
    private double lo = 114.25195720590678;
    private ActivityMainBinding binding;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private GeoCoder mGeoCoder;

    // 【优化点1】声明一个成员变量，用于记住当前点击添加的 Marker
    private Marker mCurrentMarker;
    // 【优化点2】声明一个成员变量，用于提前缓存 Marker 图标
    private BitmapDescriptor mCustomIcon;
    private ICustomService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = CustomService.getService();
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        // 1. 初始化地图
        mBaiduMap = binding.bmapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        // 【优化点2】在初始化时提前加载图标，避免每次点击都去读取图片
        mCustomIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_positioning);

        // 2. 初始化反地理编码器
        mGeoCoder = GeoCoder.newInstance();
        // 3. 设置地图点击监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double la = latLng.latitude;
                double lo = latLng.longitude;
                runOnUiThread(() -> binding.text.setText(String.format("经度：%s 纬度：%s", la, lo)));
                // 【优化点1】只移除上一次的自定义 Marker，不再使用 clear() 清空全图
                if (mCurrentMarker != null) {
                    mCurrentMarker.remove();
                }
                // 2) 添加新的 Marker
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(mCustomIcon)
                        .anchor(0.5f, 1.0f); // 直接使用提前加载好的图标
                mCurrentMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
                // 3) 将地图中心点平滑移动到你点击的位置
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                setLatLng(la, lo);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
            }
        });

        // 4. 初始化定位 SDK
        try {
            mLocationClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setScanSpan(1000);
            option.setIsNeedAddress(false);
            mLocationClient.setLocOption(option);

            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    if (location == null || mBaiduMap == null) return;
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();
                    mBaiduMap.setMyLocationData(locData);
                }
            });

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            } else {
                mLocationClient.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (service != null) {
            try {
                com.example.hookgps.model.LatLng latLng = service.getLatng();
                la = latLng.latitude;
                lo = latLng.longitude;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        LatLng latLng = new LatLng(la, lo);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(mCustomIcon); // 直接使用提前加载好的图标
        mCurrentMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
        // 3) 将地图中心点平滑移动到你点击的位置
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        binding.text.setText(String.format("经度：%s 纬度：%s", la, lo));
    }


    private void setLatLng(double latitude, double longitude) {
        if (service == null) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "设置失败，系统服务不存在", Toast.LENGTH_SHORT).show());
        } else {
            try {
                service.setLatng(new com.example.hookgps.model.LatLng(latitude, longitude));
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show());
            } catch (RemoteException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "设置失败", Toast.LENGTH_SHORT).show());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.bmapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.bmapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        if (mGeoCoder != null) {
            mGeoCoder.destroy();
        }
        // 【优化点3】在页面销毁时，回收提前加载的图标资源，防止内存泄漏
        if (mCustomIcon != null) {
            mCustomIcon.recycle();
        }
    }

}