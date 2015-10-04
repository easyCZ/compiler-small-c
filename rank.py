import glob
import re

SCOREBOARD_DIR = 'scoreboard/'
FILE_TYPE = 'html'
HEADER_ROW_COUNT = 6
PASS = 'Pass'
TABLE_ROW = '<tr>'

def main():
    ranks = []
    row_count = None
    for filename in glob.glob('%s*.%s' % (SCOREBOARD_DIR, FILE_TYPE)):
        with open(filename, 'r') as f:
            text = f.read()

            if not row_count:
                row_count = len([m.start() for m in re.finditer(TABLE_ROW, text)]) - HEADER_ROW_COUNT

            pass_count = len([m.start() for m in re.finditer(PASS, text)])
            ranks.append((filename, pass_count))

    ranks.sort(key=lambda x: x[1], reverse=True)
    for rank in ranks:
        print rank[0].replace(SCOREBOARD_DIR, '').replace('.%s' % FILE_TYPE, ''), '%i / %i' % (rank[1], row_count)


if __name__ == "__main__":
    main()