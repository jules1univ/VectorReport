import sys
import faker
from multiprocessing import Pool, cpu_count
from math import ceil


def get_output_file(raw: str):
    return raw.strip()


def get_output_size(raw: str) -> tuple[int, bool] | tuple[None, None]:
    is_datasize = raw.lower().endswith("kb") or raw.lower().endswith(
        "mb") or raw.lower().endswith("gb")
    if not is_datasize:
        try:
            return int(raw), is_datasize
        except ValueError:
            return None, None
    try:
        remove_ending = raw[:-2] if is_datasize else raw
        size = int(remove_ending)
        if raw.lower().endswith("kb"):
            size *= 1024
        elif raw.lower().endswith("mb"):
            size *= 1024 * 1024
        elif raw.lower().endswith("gb"):
            size *= 1024 * 1024 * 1024
        return size, is_datasize
    except ValueError:
        return None, None


def generate_chunk_rows(args: tuple[int, int, int]) -> str:
    start_id, count, seed = args
    fake = faker.Faker()
    faker.Faker.seed(seed)
    lines = []
    for i in range(count):
        lines.append(
            f"{start_id + i},{fake.name()},{fake.email()},{fake.random_int(min=18, max=80)},{fake.date()}\n"
        )
    return "".join(lines)


def generate_chunk_size(args: tuple[int, int, int]) -> tuple[str, int]:
    start_id, target_bytes, seed = args
    fake = faker.Faker()
    faker.Faker.seed(seed)
    lines = []
    current_size = 0
    id_counter = start_id
    while current_size < target_bytes:
        line = f"{id_counter},{fake.name()},{fake.email()},{fake.random_int(min=18, max=80)}\n"
        lines.append(line)
        current_size += len(line.encode('utf-8'))
        id_counter += 1
    return "".join(lines), id_counter - start_id


def generate_rows(output_file: str, total_rows: int):
    workers = cpu_count()
    chunk_size = ceil(total_rows / workers)

    chunks = [
        (i * chunk_size + 1, min(chunk_size, total_rows - i * chunk_size), i * 1337)
        for i in range(workers)
        if i * chunk_size < total_rows
    ]

    print(f"Generating {total_rows} rows using {workers} workers.")

    with Pool(workers) as pool:
        results = pool.map(generate_chunk_rows, chunks)

    with open(output_file, 'w', buffering=8 * 1024 * 1024) as f:
        f.write("id,name,email,age,date\n")
        written = 0
        for chunk in results:
            f.write(chunk)
            written += chunk.count('\n')
            print(f"{written}/{total_rows}", end='\r')
    print(f"\nDone: {total_rows} rows written to {output_file}")


def generate_size(output_file: str, target_size: int):
    workers = cpu_count()
    chunk_target = target_size // workers

    print(f"Generating {target_size} bytes using {workers} workers.")

    chunks = [
        (i * 100_000 + 1, chunk_target, i * 1337)
        for i in range(workers)
    ]

    with Pool(workers) as pool:
        results = pool.map(generate_chunk_size, chunks)

    all_lines = []
    id_counter = 1
    total_written = 0

    for chunk_content, _ in results:
        for line in chunk_content.splitlines():
            if total_written >= target_size:
                break
            rest = line.split(',', 1)[1] if ',' in line else line
            new_line = f"{id_counter},{rest}\n"
            all_lines.append(new_line)
            total_written += len(new_line.encode('utf-8'))
            id_counter += 1
        if total_written >= target_size:
            break

    print("Writing to file.")
    with open(output_file, 'w', buffering=8 * 1024 * 1024) as f:
        f.write("id,name,email,age\n")
        f.writelines(all_lines)

    print(f"\nDone: {total_written} bytes written to {output_file}")


def main():
    args = sys.argv[1:]
    if len(args) != 2:
        print("Usage: python generate_csv.py <output_file.csv> <rows|size>")
        return 1

    output_file = get_output_file(args[0])
    if output_file is None:
        print("Invalid output file name.")
        return 1

    size, is_datasize = get_output_size(args[1])
    if size is None:
        print("Invalid size. Use a number followed by 'KB', 'MB', 'GB' for size or just a number for rows.")
        return 1

    try:
        if not is_datasize:
            generate_rows(output_file, size)
        else:
            generate_size(output_file, size)
    except KeyboardInterrupt:
        pass
    return 0


if __name__ == "__main__":
    sys.exit(main())
