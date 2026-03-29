
int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;


int main(int argc, char **argv) {
  if (argc < 2)
    exit(1);

  double zoom_factor = 1;
  int skip_iterations = 0;
  int coordinate_file_idx = 1;
  if (argc >= 3) {
    coordinate_file_idx++;
    if (sscanf(argv[1], "%lf", &zoom_factor) != 1) {
      perror("sscanf");
      exit(1);
    }

    if (argc == 4) {
      if (sscanf(argv[2], "%d", &skip_iterations) != 1) {
        perror("sscanf");
        exit(1);
      }
      coordinate_file_idx++;
    }
  }

  FILE *f = fopen(argv[coordinate_file_idx], "r");
  if (!f) {
    perror("fopen");
    exit(1);
  }

  int num_objs;

  if (fscanf(f, "%d", &num_objs) != 1) {
    perror("fscanf");
    exit(1);
  }

  double x;
  double y;

  SDL_Event event;
  SDL_Renderer *renderer;
  SDL_Window *window;

  SDL_Init(SDL_INIT_VIDEO);
  SDL_CreateWindowAndRenderer(WINDOW_WIDTH, WINDOW_WIDTH, 0, &window, &renderer);

  int stop = 0;
  int iterations = 0;

  while (!stop && event.type != SDL_QUIT) {
    memset(&event, 0, sizeof(SDL_Event));

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0);
    SDL_RenderClear(renderer);

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

    for (int i = 0; i < num_objs; i++) {
      if (fscanf(f, "%lf %lf", &x, &y) != 2) {
        stop = 1;
        break;
      }

      if (skip_iterations <= 0) {
        drawPoint(renderer, zoom_factor, x, y);
      }
    }

    SDL_RenderPresent(renderer);

    iterations++;
    printf("Iteration %d\n", iterations);

    while (skip_iterations <= 0 && event.type != SDL_QUIT && event.type != SDL_KEYDOWN) {
      SDL_WaitEvent(&event);
    }

    skip_iterations--;
  }

  printf("Done. %d iterations total.\n", iterations);

  fclose(f);

  while (event.type != SDL_QUIT) {
    SDL_WaitEvent(&event);
  }

  SDL_DestroyRenderer(renderer);
  SDL_DestroyWindow(window);
  SDL_Quit();

  return EXIT_SUCCESS;
}

typedef uint_fast32_t index;

