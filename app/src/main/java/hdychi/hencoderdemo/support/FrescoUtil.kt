package hdychi.hencoderdemo.support

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder


fun SimpleDraweeView.showThumb(url : String,resizeWidthDp : Int, resizeHeight : Int){
        if(url==""){
            return
        }
        val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(ResizeOptions(resizeWidthDp,resizeHeight))
                .setProgressiveRenderingEnabled(true)
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build()
        this.controller = controller
}
