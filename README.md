# SKMap
un composant Skot pour afficher/contrôler une map et ses markers
Pour l'instant seule la version android "legacy" est disponible

Elle est basée sur GoogleMap

## Utilisation

Pour utiliser la librairie vous devez ajouter la ligne suivante à votre fichier _skot_librairies.properties_

`tech.skot.libraries.sk-map:0.0.14_1.1.48:viewlegacy,tech.skot.libraries.map.di.skmapModule` pour utiliser google map
`tech.skot.libraries.sk-map:0.0.14_1.1.48:viewlegacy_mapbox,tech.skot.libraries.map.di.skmapModule` pour utiliser mapbox

Cette librairie consiste en un seul SKComponent:

son ViewContract : [SKMapVC](/documentation/gfm/viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/index.md)

son ViewModel (open) : [SKMap](/documentation/gfm/viewmodel/viewmodel/tech.skot.libraries.map/-s-k-map/index.md)

[documentation](documentation/gfm/index.md)