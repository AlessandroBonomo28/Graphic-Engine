# JAVA Graphic Engine (Not very optimized)
I made this engine to understand how 3D graphics works. I started from a simple drawline function and progressively increased the complexity.
# The basic principle of plotting a dot in 3D space

In a 2D world, objects have no depth. The observer sees a portion of the world trough a rectangle and the objects can be represented using X,Y coordinates.

![1](https://user-images.githubusercontent.com/75626033/217098868-d1d4e6e7-c7b6-4569-97d8-4bc2e4342c78.png)

In 3D things start to get a bit harder: now objects have depth. This means that objects will get bigger as they approach the observer. The observer sees the world trough a **frustum** instead of a 2D rectangle. Objects now have 3 components: X,Y,Z. 

![2](https://user-images.githubusercontent.com/75626033/217098875-6fadc43d-a698-4e55-b96d-3d2ddd7c7724.png)

![3](https://user-images.githubusercontent.com/75626033/217099229-cda30f3f-d57d-481b-ad55-cfbf2339bd50.png)

- Visualization of a **Frustum**

![viewing frustum](https://user-images.githubusercontent.com/75626033/217095508-80a96407-1ea6-4027-b686-8a27700aa156.JPG)
