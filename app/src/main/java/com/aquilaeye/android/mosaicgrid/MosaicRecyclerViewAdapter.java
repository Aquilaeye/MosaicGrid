package com.aquilaeye.android.mosaicgrid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MosaicRecyclerViewAdapter extends RecyclerView.Adapter<MosaicViewHolders> {
    final static int span = 40;
    private List<ImageItem> itemList;
    private Context context;
    private GridLayoutManager mosiacLayout;
    private List<ImageInfo> imageInfo;

    public MosaicRecyclerViewAdapter(Context context, List<ImageItem> itemList) {
        if (itemList.get(0) instanceof ImageItem) {
            Log.e("ARchit", "MosaicRecyclerViewAdapter: ");
        }
        this.itemList = itemList;
        this.context = context;
        this.imageInfo = new ArrayList<ImageInfo>();
        for (ImageItem imageItem : itemList) {
            this.imageInfo.add(getImageInfo(context.getResources(), imageItem.getPhoto()));
        }
        //mosiacLayout = new StaggeredGridLayoutManager(2,GridLayoutManager.VERTICAL);
        mosiacLayout = new GridLayoutManager(context, span);
        mosiacLayout.setSpanSizeLookup(new MosaicSpan());
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getImgPref();
    }

    @Override
    public MosaicViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, null);
        MosaicViewHolders rcv = new MosaicViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(MosaicViewHolders holder, int position) {
        int width = (int) (mosiacLayout.getWidth() * (imageInfo.get(position).getSpanSizeLookup() / (float) span));
        holder.countryPhoto.setImageBitmap(decodeScaledBitmapFromResource(context.getResources(), itemList.get(position).getPhoto(), Math.round(width / imageInfo.get(position).getAspectRatio())));
        //holder.countryPhoto.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), itemList.get(position).getPhoto(), width,  (int)(width*imageInfo.get(position).getAspectRatio())));
//       holder.countryPhoto.setImageResource(itemList.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mosiacLayout;
    }


    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 1;
            final int halfWidth = width / 1;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize += 1;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeScaledBitmapFromResource(Resources res, int resId, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmapImage = BitmapFactory.decodeResource(res, resId, options);
        int nh = (int) (options.outWidth * ((double) reqHeight / options.outHeight));
        return Bitmap.createScaledBitmap(bitmapImage, nh, reqHeight, true);
    }

    public ImageInfo getImageInfo(Resources res, int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeResource(res, resId, options);
        return new ImageInfo(options.outWidth / (float) options.outHeight, options.outWidth / 3, options.outHeight / 3);
    }

    protected class MosaicSpan extends GridLayoutManager.SpanSizeLookup {
        int viewport_width;
        int ideal_height;
        boolean changes;

        MosaicSpan() {
            viewport_width = 0;
            ideal_height = 0;
            changes = true;
        }

        @Override
        public int getSpanSize(int position) {
            if (changes) {
                GenerateSpanLookup();
                changes = false;
            }
            return imageInfo.get(position).getSpanSizeLookup();

        }

        private void GenerateSpanLookup() {
            viewport_width = mosiacLayout.getWidth();
            ideal_height = (mosiacLayout.getHeight() / 2);
            float summed_width = summedWidth(imageInfo, ideal_height);
            int rows = Math.round(summed_width / viewport_width);
            if (rows < 1) {
                float summed_ratios = 0;
                for (ImageInfo info : imageInfo) {
                    summed_ratios += info.getAspectRatio();
                }
                for (ImageInfo info : imageInfo) {
                    info.setSpanSizeLookup(Math.round(span / summed_ratios * info.getAspectRatio()));
                }
            } else {
                int[] seq = CreateSequence(imageInfo);
                int[] partition = linear_partition(seq, rows);
                int index = 0;
                for (int column : partition) {
                    float summed_ratios = 0;
                    for (int i = 0; i < column; i++) {
                        summed_ratios += imageInfo.get(index + i).getAspectRatio();
                    }
                    int summed_span = 0;
                    for (int i = 0; i < column; i++) {
                        int a = Math.round(span / summed_ratios * imageInfo.get(index + i).getAspectRatio());
                        imageInfo.get(index + i).setSpanSizeLookup(a);
                        summed_span += a;
                    }
                    if (summed_span > span) {
                        int temp = 0;
                        for (int i = 0; i < column - 1; i++) {
                            temp = imageInfo.get(index + i).getSpanSizeLookup() > imageInfo.get(index + i + 1).getSpanSizeLookup() ? i : i + 1;
                        }
                        imageInfo.get(index + temp).setSpanSizeLookup(imageInfo.get(index + temp).getSpanSizeLookup() - (summed_span - span));
                    }
                    if (summed_span < span) {
                        int temp = 0;
                        for (int i = 0; i < column - 1; i++) {
                            temp = imageInfo.get(index + i).getSpanSizeLookup() < imageInfo.get(index + i + 1).getSpanSizeLookup() ? i : i + 1;
                        }
                        imageInfo.get(index + temp).setSpanSizeLookup(imageInfo.get(index + temp).getSpanSizeLookup() - (summed_span - span));
                    }
                    index += column;
                }
            }
        }

        public float summedWidth(List<ImageInfo> imageinfo, int ideal_height) {
            float sum = 0;
            for (ImageInfo info : imageinfo) {
                sum += info.getAspectRatio() * ideal_height;
            }
            return sum;
        }

        public int[] CreateSequence(List<ImageInfo> imageinfo) {
            int[] seq = new int[imageinfo.size()];
            int i = 0;
            for (ImageInfo info : imageinfo) {
                seq[i] = (int) (info.getAspectRatio() * 100);
                i++;
            }
            return seq;
        }

        public int[] linear_partition(int[] seq, int rows) {
            if (rows < 0) {
                int[] partition = {seq.length};
                return partition;
            }
            if (rows > seq.length) {
                int[] partition = new int[seq.length];
                for (int i = 0; i < seq.length; i++) {
                    partition[i] = 1;
                }
                return partition;
            }
            int[] partition = new int[rows];
            int n = seq.length;

            int[][] table = new int[n][rows];
            int[][] solution = new int[n - 1][rows - 1];

            for (int i = 0; i < n; i++) {
                table[i][0] = seq[i] + (i > 0 ? table[i - 1][0] : 0);
            }
            for (int i = 0; i < rows; i++) {
                table[0][i] = seq[0];
            }
            for (int i = 1; i < n; i++) {
                for (int j = 1; j < rows; j++) {

                    int[] m = {0, 0};
                    int[] temp = new int[2];
                    m[0] = Math.max(table[0][j - 1], table[i][0] - table[0][0]);
                    m[1] = 0;
                    for (int x = 1; x < i; x++) {
                        temp[0] = Math.max(table[x][j - 1], table[i][0] - table[x][0]);
                        temp[1] = x;
                        if (m[0] >= temp[0]) {
                            m[0] = temp[0];
                            m[1] = temp[1];
                        }
                    }
                    table[i][j] = m[0];
                    solution[i - 1][j - 1] = m[1];

                }
            }
            int index = rows;
            n = n - 1;
            rows = rows - 2;
            while (rows >= 0) {
                partition[index - 1] = 0;
                for (int i = solution[n - 1][rows] + 1; i < n + 1; i++) {
                    partition[index - 1]++;
                }
                n = solution[n - 1][rows];
                rows = rows - 1;
                index--;
            }
            partition[index - 1] = 0;
            for (int i = 0; i < n + 1; i++) {
                partition[index - 1]++;
            }
            return partition;
        }

    }

    public class ImageInfo {
        private float AspectRatio;
        private int Width;
        private int Height;
        private int spanSizeLookup;

        ImageInfo() {
            AspectRatio = 0;
            Width = 0;
            Height = 0;
            spanSizeLookup = -1;
        }

        ImageInfo(float aspectRatio, int width, int height) {
            AspectRatio = aspectRatio;
            this.Height = height;
            this.Width = width;
            spanSizeLookup = -1;
        }

        public float getAspectRatio() {
            return AspectRatio;
        }

        public int getHeight() {
            return Height;
        }

        public int getWidth() {
            return Width;
        }

        public int getSpanSizeLookup() {
            return spanSizeLookup;
        }

        public void setAspectRatio(float aspectRatio) {
            this.AspectRatio = aspectRatio;
        }

        public void setHeight(int height) {
            this.Height = height;
        }

        public void setWidth(int width) {
            this.Width = width;
        }

        public void setSpanSizeLookup(int spanSizeLookup) {
            this.spanSizeLookup = spanSizeLookup;
        }
    }

}
