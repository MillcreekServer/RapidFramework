package io.github.wysohn.rapidframework4.bukkit.utils;

import io.github.wysohn.rapidframework4.utils.trie.StringListTrie;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialTrie extends StringListTrie {
    private static MaterialTrie trie;

    public static MaterialTrie singleton() {
        if (trie == null) {
            trie = new MaterialTrie();
        }

        return trie;
    }

    private MaterialTrie() {
        super(Arrays.stream(Material.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

    public List<Material> findMaterialsStartsWith(String str) {
        List<String> names = getAllStartsWith(str);
        if (names == null)
            return null;

        return names.stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }

//    public static void main(String[] ar){
//        MaterialTrie trie = new MaterialTrie();
//        try(Scanner sc = new Scanner(System.in)){
//            do{
//                System.out.print("Material? >> ");
//                String input = sc.nextLine();
//
//                System.out.println("Result:");
//                trie.getAllStartsWith(input).forEach(System.out::println);
//            }while(sc.hasNextLine());
//        }
//    }
}
