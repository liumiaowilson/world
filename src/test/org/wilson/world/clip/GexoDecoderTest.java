package org.wilson.world.clip;

import org.junit.Test;

public class GexoDecoderTest {

    @Test
    public void test() {
        String url = "aqqv$==cil2m\u0026cyzvaplqdx\u0026cfn=nfb=25942=2594216_v360\u0026nv4?leb(20160811043908^lem(20160811083908^zx(-1^rx(1071^zlq(2506752b^amra(0bd75id504bg2c48135i5";
        System.out.println(GexoDecoder.decode(url));
    }

}
