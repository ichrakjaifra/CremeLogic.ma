package ma.cremelogic.CremeLogic.ma.enums;

public enum UniteMesure {
    GRAMME("g"),
    KILOGRAMME("kg"),
    LITRE("L"),
    MILLILITRE("mL"),
    UNITE("unité"),
    BOITE("boîte"),
    SAC("sac"),
    CARTON("carton");

    private final String symbole;

    UniteMesure(String symbole) {
        this.symbole = symbole;
    }

    public String getSymbole() {
        return symbole;
    }
}