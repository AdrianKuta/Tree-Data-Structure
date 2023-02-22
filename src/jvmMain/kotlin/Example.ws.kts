import com.github.adriankuta.datastructure.tree.tree

val root =
    tree("World") {
        child("North America") {
            child("USA")
        }
        child("Europe") {
            child("Poland")
            child("Germany")
        }
    }

print(root.prettyString())