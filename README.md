# JAVA Graphic Engine (Not very optimized)
I made this engine to understand how 3D graphics works. I started from a simple drawline function and progressively increased the complexity.
# The basic principle of plotting a dot in 3D space

In a 2D world, objects have no depth. The observer sees a portion of the world trough a rectangle and the objects can be represented using X,Y coordinates.

![1](https://user-images.githubusercontent.com/75626033/217095591-3cff1bb9-8f7d-4633-89fb-5aef558b6a2a.png)

In 3D things start to get a bit harder: now objects have depth. This means that objects will get bigger as they approach the observer. The observer sees the world trough a **frustum** instead of a 2D rectangle. Objects now have 3 components: X,Y,Z. 

![2](https://user-images.githubusercontent.com/75626033/217095607-30f498fc-d50a-4dee-a352-cc26b5c6d795.png)

![3](https://user-images.githubusercontent.com/75626033/217095618-ebf3b609-cc7a-474b-aacc-29dac7d3b83d.png)

- Visualization of a frustum

![viewing frustum](https://user-images.githubusercontent.com/75626033/217095508-80a96407-1ea6-4027-b686-8a27700aa156.JPG)
