# 이슈
- chromadb는 pydantic 종속성에 따른 fastapi와의 버전 충돌 문제로 사용하지 않기로 결정 ->FAISS
- embedding.py
  - faissdb로 임베딩하는 코드


# req
```
req = {
        "food": [400, 10000],
        "traffic": [7000, 10000],
        "online": [0, 10000],
        "offline": [9000, 10000],
        "cafe": [0, 10000],
        "housing": [0, 10000],
        "fashion": [12000, 10000],
        "culture": [0, 10000]
        }
```
# res
```
res = {
     "message" = "식비는 예산 범위인 10,000원 내에서 지출하였으며, 교통비는 7,000원으로 예산 범위 내에서 진행되었습니다. 온라인 쇼핑은 예산을 초과하지 않고, 오프라인 쇼핑은 9,000원으로 예산 범위 내에서 이루어졌습니다. 카페/간식 카테고리는 예산 내에서 사용되지 않았습니다. 주거/통신과 문화/여가 카테고리는 예산 범위 내에서 소비가 이루어지지 않았습니다. 패션/미용 카테고리는 12,000원으로 예산을 초과하였습니다. 총 소비액은 38,400원입니다. 주어진 상황에서 가장 적합한 카드는 삼성카드 taptap O입니다. 이 카드는 오프라인 쇼핑, 교통비에 할인 혜택을 제공하여 예산 내에서 소비를 제어할 수 있으며, 스타벅스에서 50% 할인 혜택을 받을 수 있습니다."
    }
```
