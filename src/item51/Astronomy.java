package item51;

public class Astronomy {

    public enum CelestialBody {
        STAR, PLANET // it can be added freer than boolean
        // COMET, BLACK_HOLE ... etc are available!
    }

    // This would be better than boolean parameter
    public static void light(CelestialBody astro) {
        if(astro == CelestialBody.PLANET)
            System.out.println("sparkle");
    }

    public static void light(boolean isStar) {
        if(isStar)
            System.out.println("sparkle");
    }

}
